import { computed, inject, Injectable, signal } from '@angular/core';
import { forkJoin, of, switchMap, tap } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ApiService } from './api.service';
import { AuthService } from './auth.service';
import { DashboardResponse, Delivery, DeliveryDocument, DeliveryProof, DeliveryStatus, ExceptionItem, Hub, ManagedUser, MonitorDelivery, ReportItem, ReportType, TrackingEvent } from '../models';

@Injectable({ providedIn: 'root' })
export class DataService {
  // api and auth service injections
  private readonly api = inject(ApiService);
  private readonly auth = inject(AuthService);
  private readonly userStatusKey = 'smartcourier.user-status';
  
  // global UI state signals
  readonly loading = signal(false);
  readonly error = signal('');
  readonly success = signal('');
  
  // data store signals
  readonly deliveries = signal<Delivery[]>([]);
  readonly monitor = signal<MonitorDelivery[]>([]);
  readonly hubs = signal<Hub[]>([]);
  readonly tracking = signal<Record<string, TrackingEvent[]>>({});
  readonly documents = signal<Record<string, DeliveryDocument[]>>({});
  readonly proofs = signal<Record<string, DeliveryProof | null>>({});
  readonly proofImages = signal<Record<string, string | null>>({});
  readonly exceptions = signal<ExceptionItem[]>([]);
  readonly reports = signal<ReportItem[]>([]);
  readonly users = signal<ManagedUser[]>([]);
  readonly adminStats = signal<DashboardResponse | null>(null);

  // computed customer dashboard stats
  readonly customerStats = computed(() => {
    const list = this.deliveries();
    return {
      total: list.length,
      active: list.filter(d => !['DELIVERED', 'FAILED', 'RETURNED'].includes(d.status)).length,
      delivered: list.filter(d => d.status === 'DELIVERED').length,
      exceptions: list.filter(d => ['DELAYED', 'FAILED', 'RETURNED'].includes(d.status)).length
    };
  });
  // load all deliveries for logged-in customer
  loadCustomer(): void {
    this.wrap(this.api.get<Delivery[]>('/deliveries/my').pipe(tap(list => this.deliveries.set(list))));
  }
  // load a single delivery by id
  loadDelivery(id: number): void {
    this.wrap(this.api.get<Delivery>(`/deliveries/${id}`).pipe(tap(item => this.upsertDelivery(item))));
  }
  // load tracking timeline for a delivery
  loadTimeline(trackingNumber: string): void {
    this.wrap(this.api.get<TrackingEvent[]>(`/tracking/${trackingNumber}`).pipe(
      catchError(() => of([])),
      tap(events => this.setTimeline(trackingNumber, events))
    ));
  }
  // load documents attached to a delivery
  loadDocuments(deliveryId: string): void {
    this.wrap(this.api.get<DeliveryDocument[]>(`/tracking/documents/${deliveryId}`).pipe(
      catchError(() => of([])),
      tap(documents => this.setDocuments(deliveryId, documents))
    ));
  }
  // load proof of delivery
  loadProof(deliveryId: string): void {
    this.wrap(this.api.get<DeliveryProof>(`/tracking/${deliveryId}/proof`).pipe(
      catchError(() => of(null)),
      tap(proof => {
        // Updates proofs signal immutably.
        this.proofs.update(map => ({ ...map, [deliveryId]: proof }));
        if (proof?.proofImagePath) this.loadProofImage(deliveryId);
      })
    ));
  }
  // download proof image as blob
  loadProofImage(deliveryId: string): void {
    this.api.blob(`/tracking/${deliveryId}/proof/image`).pipe(
      catchError(() => of(null))
    ).subscribe(blob => {
      const existing = this.proofImages()[deliveryId];
      // Removes old memory reference. Prevents memory leaks
      if (existing) URL.revokeObjectURL(existing);
      // Creates browser-readable image URL.
      this.proofImages.update(map => ({ ...map, [deliveryId]: blob ? URL.createObjectURL(blob) : null }));
    });
  }
  // create a new delivery and sync with admin monitor
  createDelivery(body: Omit<Delivery, 'id' | 'trackingNumber' | 'status' | 'charge' | 'createdAt' | 'updatedAt'>, afterSuccess?: (delivery: Delivery) => void): void {
    let created: Delivery | null = null;
    this.wrap(this.api.post<Delivery>('/deliveries', body).pipe(
      tap(delivery => { created = delivery; this.upsertDelivery(delivery); }),
      switchMap(delivery => this.syncMonitor(delivery).pipe(catchError(() => of(null))))
    ), 'Delivery created and monitor synchronized', () => { if (created) afterSuccess?.(created); });
  }
  // upload a document file for a delivery
  uploadDocument(input: { deliveryId: string; trackingNumber: string; documentType: string; uploadedBy: string; file: File }, afterSuccess?: () => void): void {
    const form = new FormData();
    Object.entries(input).forEach(([key, value]) => form.append(key, value));
    this.wrap(this.api.upload<DeliveryDocument>('/tracking/documents/upload', form).pipe(
      tap(document => this.documents.update(map => ({ ...map, [document.deliveryId]: [document, ...(map[document.deliveryId] ?? [])] })))
    ), 'Document uploaded and attached to delivery', afterSuccess);
  }
  // submit proof of delivery and mark as delivered
  submitProof(input: { deliveryId: string; trackingNumber: string; receivedBy: string; submittedBy: string; remarks?: string; proofImage?: File | null }, afterSuccess?: () => void): void {
    const form = new FormData();
    form.append('deliveryId', input.deliveryId);
    form.append('trackingNumber', input.trackingNumber);
    form.append('receivedBy', input.receivedBy);
    form.append('submittedBy', input.submittedBy);
    if (input.remarks) form.append('remarks', input.remarks);
    if (input.proofImage) form.append('proofImage', input.proofImage);

    this.wrap(this.api.upload<DeliveryProof>('/tracking/proof', form).pipe(
      switchMap(proof => this.api.put<MonitorDelivery>(`/admin/deliveries/${proof.deliveryId}/status`, null, { status: 'DELIVERED' }).pipe(
        catchError(() => of(null)),
        switchMap(() => this.api.put<Delivery>(`/deliveries/${proof.deliveryId}/status`, { status: 'DELIVERED' }).pipe(catchError(() => of(null)))),
        tap(updated => { if (updated) this.upsertDelivery(updated); else this.patchDeliveryStatus(proof.deliveryId, 'DELIVERED'); }),
        switchMap(() => this.api.post<TrackingEvent>('/tracking/events', {
          deliveryId: proof.deliveryId,
          trackingNumber: proof.trackingNumber,
          status: 'DELIVERED',
          location: 'Recipient address',
          remarks: input.remarks || `Proof received by ${input.receivedBy}`,
          updatedBy: input.submittedBy
        }).pipe(catchError(() => of(null)))),
        tap(event => { if (event) this.appendTracking(event); }),
        switchMap(() => of(proof))
      )),
      tap(proof => {
        this.proofs.update(map => ({ ...map, [proof.deliveryId]: proof }));
        if (proof.proofImagePath) this.loadProofImage(proof.deliveryId);
        this.patchDeliveryStatus(proof.deliveryId, 'DELIVERED');
      })
    ), 'Delivery proof submitted', afterSuccess);
  }
  // load all admin data (stats, deliveries, monitor, hubs, exceptions, users)
  loadAdmin(): void {
    this.wrap(forkJoin({
      stats: this.api.get<DashboardResponse>('/admin/dashboard').pipe(catchError(() => of(null))),
      deliveries: this.api.get<Delivery[]>('/deliveries').pipe(catchError(() => of([]))),
      monitor: this.api.get<MonitorDelivery[]>('/admin/deliveries').pipe(catchError(() => of([]))),
      hubs: this.api.get<Hub[]>('/admin/hubs/all').pipe(catchError(() => of([]))),
      exceptions: this.api.get<ExceptionItem[]>('/admin/exceptions/all').pipe(catchError(() => of([]))),
      users: this.api.get<ManagedUser[]>('/admin/users').pipe(catchError(() => of([])))
    }).pipe(tap(({ stats, deliveries, monitor, hubs, exceptions, users }) => {
      const enrichedMonitor = monitor.map(m => {
        const d = deliveries.find(x => String(x.id) === m.deliveryId);
        if (d) {
          m.senderCity = m.senderCity || d.senderAddress?.city;
          m.receiverCity = m.receiverCity || d.receiverAddress?.city;
          if (!m.customerName || !isNaN(Number(m.customerName))) {
            const user = users.find(u => String(u.userId) === d.customerId);
            m.customerName = user ? user.fullName : d.customerId;
          }
        }
        return m;
      });
      this.adminStats.set(stats);
      this.deliveries.set(deliveries);
      this.monitor.set(enrichedMonitor);
      this.hubs.set(hubs);
      this.exceptions.set(exceptions);
      this.users.set(users.map(u => this.normalizeUserStatus(u)));
    })));
  }
  // create a new hub
  createHub(hub: Omit<Hub, 'id' | 'active' | 'createdAt'>): void {
    this.wrap(this.api.post<Hub>('/admin/hubs', hub).pipe(tap(item => this.hubs.update(list => [item, ...list]))), 'Hub created');
  }
  // update delivery status across monitor, delivery, and tracking
  updateStatus(item: MonitorDelivery | Delivery, status: DeliveryStatus, location: string, remarks: string): void {
    this.wrap(this.statusUpdateRequest(item, status, location, remarks).pipe(
      switchMap(() => this.autoRaiseForStatus(item, status, remarks))
    ), this.isExceptionStatus(status) ? 'Status updated and exception raised' : 'Status updated across monitor, delivery, and tracking');
  }
  // add a standalone tracking event
  addTrackingEvent(item: MonitorDelivery | Delivery, status: DeliveryStatus, location: string, remarks: string): void {
    const deliveryId = 'deliveryId' in item ? item.deliveryId : String(item.id);
    this.wrap(this.api.post<TrackingEvent>('/tracking/events', {
      deliveryId,
      trackingNumber: item.trackingNumber,
      status,
      location,
      remarks,
      updatedBy: this.auth.user()?.email ?? 'admin'
    }).pipe(tap(event => this.appendTracking(event))), 'Tracking event added');
  }
  // raise an exception on a delivery
  raiseException(delivery: MonitorDelivery, exceptionStatus: DeliveryStatus, reason: string): void {
    this.wrap(this.api.post<ExceptionItem>('/admin/exceptions', {
      deliveryId: delivery.deliveryId, trackingNumber: delivery.trackingNumber, exceptionStatus, reason
    }).pipe(tap(item => this.exceptions.update(list => [item, ...list]))), 'Exception raised');
  }
  // resolve an exception and restore delivery to active
  resolveException(item: ExceptionItem, remarks: string): void {
    this.wrap(this.api.put<ExceptionItem>(`/admin/exceptions/${item.id}/resolve`, {
      remarks, resolvedBy: this.auth.user()?.email ?? 'admin'
    }).pipe(
      tap(updated => this.exceptions.update(list => list.map(x => x.id === updated.id ? updated : x))),
      switchMap(updated => {
        const monitor = this.monitor().find(m => m.deliveryId === updated.deliveryId);
        const delivery = this.deliveries().find(d => String(d.id) === updated.deliveryId);
        const target = monitor ?? delivery;
        if (!target) return of(updated);
        return this.statusUpdateRequest(target, 'IN_TRANSIT', monitor?.assignedHub || monitor?.receiverCity || delivery?.receiverAddress.city || 'Operations', `Exception resolved: ${remarks || updated.reason}`).pipe(catchError(() => of(updated)));
      })
    ), 'Exception resolved and delivery restored to active movement');
  }
  // generate an admin report
  generateReport(reportType: ReportType, fromDate: string, toDate: string): void {
    this.wrap(this.api.post<ReportItem>('/admin/reports/generate', null, {
      reportType, fromDate, toDate, generatedBy: this.auth.user()?.email ?? 'admin'
    }).pipe(tap(report => this.reports.update(list => [report, ...list]))), 'Report generated');
  }
  // load existing reports by type
  loadReports(reportType: ReportType): void {
    this.wrap(this.api.get<ReportItem[]>('/admin/reports', { reportType }).pipe(tap(list => this.reports.set(list))));
  }
  // activate or deactivate a user
  setUserActive(user: ManagedUser, active: boolean): void {
    const action = active ? 'activate' : 'deactivate';
    this.wrap(this.api.put<ManagedUser>(`/admin/users/${user.userId}/${action}`, {}).pipe(tap(updated => {
      this.saveUserStatus(user.userId, active);
      const normalized = { ...updated, userId: user.userId, fullName: updated.fullName || user.fullName, email: updated.email || user.email, role: updated.role || user.role, active };
      this.users.update(list => list.map(x => x.userId === user.userId ? normalized : x));
      if (!active) this.auth.markInactive(user.userId);
    })), active ? 'User activated' : 'User deactivated');
  }
  // assign a hub to a delivery
  assignHub(delivery: MonitorDelivery, assignedHub: string): void {
    this.wrap(this.persistMonitor({ ...delivery, assignedHub }).pipe(
      tap(updated => this.upsertMonitor(updated))
    ), 'Hub assignment saved');
  }
  // auto-assign hubs to unassigned deliveries
  autoAssignUnassignedHubs(): void {
    const activeHubs = this.hubs().filter(h => h.active);
    const unassigned = this.monitor().filter(m => !m.assignedHub || m.assignedHub === 'Unassigned');
    if (!activeHubs.length || !unassigned.length) {
      this.success.set(activeHubs.length ? 'No unassigned deliveries found' : 'Create an active hub before assigning deliveries');
      return;
    }
    const requests = unassigned.map(item => {
      const hub = activeHubs.find(h => h.city.toLowerCase() === item.receiverCity.toLowerCase()) ?? activeHubs[0];
      return this.persistMonitor({ ...item, assignedHub: hub.name });
    });
    this.wrap(forkJoin(requests).pipe(tap(rows => rows.forEach(row => this.upsertMonitor(row)))), 'Unassigned deliveries were matched to active hubs');
  }
  // deactivate a hub and unassign its deliveries
  deactivateHub(hub: Hub): void {
    const affected = this.monitor().filter(m => m.assignedHub === hub.name);
    this.wrap(this.api.delete<Hub>(`/admin/hubs/${hub.id}`).pipe(
      tap(updated => this.hubs.update(list => list.map(h => h.id === updated.id ? updated : h))),
      switchMap(() => affected.length ? forkJoin(affected.map(item => this.persistMonitor({ ...item, assignedHub: 'Unassigned' }))) : of([])),
      tap(rows => rows.forEach(row => this.upsertMonitor(row)))
    ), 'Hub deactivated and affected deliveries marked unassigned');
  }
  // sync delivery to admin monitor service
  private syncMonitor(delivery: Delivery) {
    const hub = this.hubs().find(h => h.active && h.city.toLowerCase() === delivery.receiverAddress.city.toLowerCase()) ?? this.hubs().find(h => h.active);
    const user = this.users().find(u => String(u.userId) === delivery.customerId);
    return this.api.post<MonitorDelivery>('/admin/deliveries/sync', {
      deliveryId: String(delivery.id),
      trackingNumber: delivery.trackingNumber,
      customerName: user ? user.fullName : delivery.customerId,
      senderCity: delivery.senderAddress.city,
      receiverCity: delivery.receiverAddress.city,
      currentStatus: delivery.status,
      assignedHub: hub?.name ?? 'Unassigned'
    }).pipe(tap(item => { if (item) this.upsertMonitor(item); }));
  }
  private persistMonitor(item: MonitorDelivery) {
    return this.api.post<MonitorDelivery>('/admin/deliveries/sync', {
      deliveryId: item.deliveryId,
      trackingNumber: item.trackingNumber,
      customerName: item.customerName,
      senderCity: item.senderCity,
      receiverCity: item.receiverCity,
      currentStatus: item.currentStatus,
      assignedHub: item.assignedHub
    });
  }
  private statusUpdateRequest(item: MonitorDelivery | Delivery, status: DeliveryStatus, location: string, remarks: string) {
    const deliveryId = 'deliveryId' in item ? item.deliveryId : String(item.id);
    const trackingNumber = item.trackingNumber;
    return this.api.put<MonitorDelivery>(`/admin/deliveries/${deliveryId}/status`, null, { status }).pipe(
      tap(updated => this.upsertMonitor(updated)),
      switchMap(() => this.api.put<Delivery>(`/deliveries/${deliveryId}/status`, { status }).pipe(catchError(() => of(null)))),
      tap(updated => { if (updated) this.upsertDelivery(updated); else this.patchDeliveryStatus(deliveryId, status); }),
      switchMap(() => this.api.post<TrackingEvent>('/tracking/events', {
        deliveryId, trackingNumber, status, location, remarks, updatedBy: this.auth.user()?.email ?? 'admin'
      })),
      tap(event => this.appendTracking(event))
    );
  }
  private autoRaiseForStatus(item: MonitorDelivery | Delivery, status: DeliveryStatus, reason: string) {
    if (!this.isExceptionStatus(status) || !('deliveryId' in item)) return of(null);
    const existingOpen = this.exceptions().some(e => e.deliveryId === item.deliveryId && e.resolutionStatus !== 'RESOLVED');
    if (existingOpen) return of(null);
    return this.api.post<ExceptionItem>('/admin/exceptions', {
      deliveryId: item.deliveryId,
      trackingNumber: item.trackingNumber,
      exceptionStatus: status,
      reason: reason || `Delivery marked ${status}`
    }).pipe(tap(exception => this.exceptions.update(list => [exception, ...list])));
  }
  private isExceptionStatus(status: DeliveryStatus): boolean {
    return ['DELAYED', 'FAILED', 'RETURNED'].includes(status);
  }
  // helper: wrap observable with loading/error/success handling
  private wrap<T>(source: import('rxjs').Observable<T>, message = '', afterSuccess?: () => void): void {
    this.loading.set(true); this.error.set(''); this.success.set('');
    source.subscribe({
      next: () => { if (message) this.success.set(message); afterSuccess?.(); },
      error: err => { this.error.set(err?.error?.message ?? err?.message ?? 'Request failed'); this.loading.set(false); },
      complete: () => this.loading.set(false)
    });
  }
  // helper: add or update a delivery in the list
  private upsertDelivery(item: Delivery): void { this.deliveries.update(list => [item, ...list.filter(x => x.id !== item.id)]); }
  // helper: add or update a monitor entry
  private upsertMonitor(item: MonitorDelivery): void { this.monitor.update(list => [item, ...list.filter(x => x.deliveryId !== item.deliveryId)]); }
  private patchDeliveryStatus(id: string, status: DeliveryStatus): void { this.deliveries.update(list => list.map(d => String(d.id) === id ? { ...d, status } : d)); }
  private setTimeline(trackingNumber: string, events: TrackingEvent[]): void { this.tracking.update(map => ({ ...map, [trackingNumber]: events })); }
  private setDocuments(deliveryId: string, documents: DeliveryDocument[]): void { this.documents.update(map => ({ ...map, [deliveryId]: documents })); }
  private normalizeUserStatus(user: ManagedUser): ManagedUser {
    const stored = this.userStatusMap()[String(user.userId)];
    return { ...user, active: stored ?? true };
  }
  private userStatusMap(): Record<string, boolean> {
    try {
      return JSON.parse(localStorage.getItem(this.userStatusKey) ?? '{}') as Record<string, boolean>;
    } catch {
      return {};
    }
  }
  private saveUserStatus(userId: number, active: boolean): void {
    localStorage.setItem(this.userStatusKey, JSON.stringify({ ...this.userStatusMap(), [String(userId)]: active }));
  }
  private appendTracking(event: TrackingEvent): void {
    this.tracking.update(map => ({ ...map, [event.trackingNumber]: [event, ...(map[event.trackingNumber] ?? [])] }));
    this.patchDeliveryStatus(event.deliveryId, event.status);
  }
}

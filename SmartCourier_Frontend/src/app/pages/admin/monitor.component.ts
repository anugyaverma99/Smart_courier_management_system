import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DataService } from '../../core/services/data.service';
import { DeliveryStatus, MonitorDelivery } from '../../core/models';
import { PaginationComponent } from '../../shared/components/pagination.component';

@Component({
  standalone: true,
  imports: [FormsModule, PaginationComponent],
  templateUrl: './monitor.component.html'
})
export class MonitorComponent implements OnInit {
  readonly data = inject(DataService);

  // search, filter, and pagination state
  search = signal('');
  status = signal<DeliveryStatus | ''>('');
  hub = signal('');
  page = signal(1);
  expandedRow = signal<string>('');

  statuses: DeliveryStatus[] = ['BOOKED','PICKED_UP','IN_TRANSIT','OUT_FOR_DELIVERY','DELIVERED','DELAYED','FAILED','RETURNED'];

  // filter monitor list by search, status, and hub
  filtered = computed(() => this.data.monitor().filter(m => {
    const q = this.search().toLowerCase();
    return (
      (!this.status() || m.currentStatus === this.status()) &&
      (!this.hub() || m.assignedHub === this.hub()) &&
      (!q || `${m.trackingNumber} ${m.customerName} ${m.senderCity} ${m.receiverCity} ${m.assignedHub}`.toLowerCase().includes(q))
    );
  }));

  pageItems = computed(() => this.filtered().slice((this.page() - 1) * 10, this.page() * 10));

  // load admin data on init
  ngOnInit(): void { this.data.loadAdmin(); }

  // update delivery status across all services
  update(m: MonitorDelivery, status: string): void {
    this.data.updateStatus(m, status as DeliveryStatus, m.assignedHub || m.receiverCity || 'Operations', `Admin moved delivery to ${status}`);
  }

  // add a manual tracking event
  addEvent(m: MonitorDelivery, status: string, location: string, remarks: string): void {
    this.data.addTrackingEvent(m, status as DeliveryStatus, location || m.assignedHub || 'Operations', remarks || `Manual ${status} update`);
  }

  // raise an exception manually
  raise(m: MonitorDelivery): void {
    this.data.raiseException(m, 'DELAYED', `Manual exception raised for ${m.trackingNumber}`);
  }

  // toggle expanded row for details
  toggleRow(id: string): void {
    this.expandedRow.set(this.expandedRow() === id ? '' : id);
  }

  // get valid next statuses based on delivery lifecycle
  getAllowedStatuses(currentStatus: DeliveryStatus | string): DeliveryStatus[] {
    const transitions: Record<string, DeliveryStatus[]> = {
      'DRAFT': ['BOOKED'],
      'BOOKED': ['PICKED_UP', 'DELAYED'],
      'PICKED_UP': ['IN_TRANSIT', 'DELAYED'],
      'IN_TRANSIT': ['OUT_FOR_DELIVERY', 'DELAYED'],
      'OUT_FOR_DELIVERY': ['DELIVERED', 'FAILED', 'DELAYED'],
      'DELIVERED': [],
      'DELAYED': ['PICKED_UP', 'IN_TRANSIT', 'OUT_FOR_DELIVERY', 'FAILED'],
      'FAILED': ['RETURNED'],
      'RETURNED': []
    };
    return transitions[currentStatus] || [];
  }
}
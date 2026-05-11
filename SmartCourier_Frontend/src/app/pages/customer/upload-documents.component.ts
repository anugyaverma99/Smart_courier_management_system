import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DataService } from '../../core/services/data.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  standalone: true,
  imports: [FormsModule],
  templateUrl: './upload-documents.component.html'
})
export class UploadDocumentsComponent implements OnInit {
  readonly data = inject(DataService); private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  
  // form fields
  deliveryId = signal(''); documentType = signal('INVOICE'); file = signal<File | null>(null);
  
  // find the selected delivery from the list
  selected = computed(() => this.data.deliveries().find(d => String(d.id) === this.deliveryId()));
  
  // load deliveries so user can pick one
  ngOnInit(): void { this.data.loadCustomer(); }
  
  // handle file selection
  // Treat this event source as a file input element”
  pick(event: Event): void { this.file.set((event.target as HTMLInputElement).files?.[0] ?? null); }
  
  // upload document to backend
  submit(): void {
    const d = this.selected(); const file = this.file();
    if (!d || !file) return;
    this.data.uploadDocument(
      { deliveryId: String(d.id), trackingNumber: d.trackingNumber, documentType: this.documentType(), uploadedBy: this.auth.user()?.email ?? '', file },
      () => void this.router.navigate(['/customer/delivery', d.id])
    );
  }
}

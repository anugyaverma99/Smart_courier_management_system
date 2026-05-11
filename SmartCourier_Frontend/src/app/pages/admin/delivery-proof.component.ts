import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DataService } from '../../core/services/data.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  standalone: true,
  imports: [FormsModule],
  templateUrl: './delivery-proof.component.html'
})
export class DeliveryProofComponent implements OnInit {
  readonly data = inject(DataService);
  private readonly auth = inject(AuthService);
  // form fields
  deliveryId = signal('');
  receivedBy = signal('');
  remarks = signal('');
  proofImage = signal<File | null>(null);
  // find the selected delivery from the list
  selected = computed(() => this.data.deliveries().find(d => String(d.id) === this.deliveryId()));
  // check if proof already exists
  existingProof = computed(() => this.deliveryId() ? (this.data.proofs()[this.deliveryId()] ?? null) : null);

  // load admin data on init
  ngOnInit(): void {
    this.data.loadAdmin();
  }

  // handle proof image selection
  pick(event: Event): void {
    this.proofImage.set((event.target as HTMLInputElement).files?.[0] ?? null);
  }

  // submit proof of delivery
  submit(): void {
    const delivery = this.selected();
    if (!delivery) return;
    this.data.submitProof({
      deliveryId: String(delivery.id),
      trackingNumber: delivery.trackingNumber,
      receivedBy: this.receivedBy(),
      submittedBy: this.auth.user()?.email ?? 'admin',
      remarks: this.remarks(),
      proofImage: this.proofImage()
    }, () => {
      this.data.loadProof(String(delivery.id));
      this.receivedBy.set('');
      this.remarks.set('');
      this.proofImage.set(null);
    });
  }
}

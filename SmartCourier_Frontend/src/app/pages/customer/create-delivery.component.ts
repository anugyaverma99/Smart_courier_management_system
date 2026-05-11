import { Component, inject, signal } from '@angular/core';
import { NgTemplateOutlet } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { DataService } from '../../core/services/data.service';
import { AddressDto, PackageDto } from '../../core/models';

const emptyAddress = (): AddressDto => ({ name: '', phone: '', email: '', addressLine: '', city: '', state: '', zipCode: '', country: 'India' });

@Component({
  standalone: true,
  imports: [FormsModule, RouterLink, NgTemplateOutlet],
  templateUrl: './create-delivery.component.html'
})
export class CreateDeliveryComponent {
  readonly data = inject(DataService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  
  // multi-step form state (step 1: sender, step 2: receiver, step 3: package)
  step = signal(1);
  sender = emptyAddress();
  receiver = emptyAddress();
  pkg: PackageDto = { description: '', weightKg: 0, lengthCm: 0, widthCm: 0, heightCm: 0, serviceType: 'domestic', declaredValue: 0 };
  pickupScheduledAt = '';
  
  // go to next step if current step is valid
  next(): void { if (this.validStep()) this.step.set(this.step() + 1); }
  
  // submit delivery to backend
  submit(): void {
    if (!this.validStep()) return;
    this.data.createDelivery(
      { customerId: this.auth.user()?.email ?? '', senderAddress: this.sender, receiverAddress: this.receiver, packageDetails: this.pkg, pickupScheduledAt: this.pickupScheduledAt },
      delivery => void this.router.navigate(['/customer/delivery', delivery.id])
    );
  }
  
  // validate current step fields
  private validStep(): boolean {
    const addressOk = (a: AddressDto) => !!(a.name && /^[0-9]{10}$/.test(a.phone) && a.addressLine && a.city && a.state && /^[0-9]{6}$/.test(a.zipCode) && a.country);
    return this.step() === 1 ? addressOk(this.sender) : this.step() === 2 ? addressOk(this.receiver) : !!(this.pkg.description && this.pkg.weightKg > 0 && this.pickupScheduledAt);
  }
}

import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CurrencyPipe } from '@angular/common';
import { DataService } from '../../core/services/data.service';
import { DeliveryStatus } from '../../core/models';
import { PaginationComponent } from '../../shared/components/pagination.component';

@Component({
  standalone: true,
  imports: [FormsModule, RouterLink, CurrencyPipe, PaginationComponent],
  templateUrl: './customer-dashboard.component.html'
})
export class CustomerDashboardComponent implements OnInit {
  readonly data = inject(DataService);
  
  // search and filter signals
  search = signal(''); status = signal<DeliveryStatus | ''>(''); page = signal(1);
  statuses: DeliveryStatus[] = ['DRAFT','BOOKED','PICKED_UP','IN_TRANSIT','OUT_FOR_DELIVERY','DELIVERED','DELAYED','FAILED','RETURNED'];
  
  // filter deliveries by search text and status
  filtered = computed(() => this.data.deliveries().filter(d => {
    const q = this.search().toLowerCase();
    return (!this.status() || d.status === this.status()) && (!q || `${d.trackingNumber} ${d.receiverAddress.name} ${d.receiverAddress.city} ${d.senderAddress.city}`.toLowerCase().includes(q));
  }));
  
  // show latest 5 deliveries
  recent = computed(() => this.data.deliveries().slice(0, 5));
  
  // show deliveries that need attention
  attention = computed(() => this.data.deliveries().filter(d => this.isBad(d.status)).slice(0, 5));
  
  // paginate filtered results (10 per page)
  pageItems = computed(() => this.filtered().slice((this.page() - 1) * 10, this.page() * 10));
  
  // load customer deliveries on page load
  ngOnInit(): void { this.data.loadCustomer(); }
  
  // check if status is problematic
  isBad(s: DeliveryStatus): boolean { return ['DELAYED','FAILED','RETURNED'].includes(s); }
}

import { Component, computed, inject, input, OnDestroy, OnInit, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DataService } from '../../core/services/data.service';

@Component({
  standalone: true,
  imports: [CurrencyPipe, DatePipe, RouterLink],
  templateUrl: './delivery-detail.component.html'
})
export class DeliveryDetailComponent implements OnInit, OnDestroy {
  id = input.required<string>();
  readonly data = inject(DataService);
  private pollId = 0;
  private attempts = signal(0);
  
  // find the delivery from the signal store
  delivery = computed(() => this.data.deliveries().find(d => String(d.id) === this.id()));
  
  // get tracking timeline for this delivery
  events = computed(() => this.delivery() ? (this.data.tracking()[this.delivery()!.trackingNumber] ?? []) : []);
  
  // get uploaded documents for this delivery
  documents = computed(() => this.data.documents()[this.id()] ?? []);
  
  // get proof of delivery
  proof = computed(() => this.data.proofs()[this.id()] ?? null);
  proofImage = computed(() => this.data.proofImages()[this.id()] ?? null);

  ngOnInit(): void {
    // load delivery details from backend
    const numeric = Number(this.id());
    if (numeric) this.data.loadDelivery(numeric);
    this.data.loadDocuments(this.id());
    this.data.loadProof(this.id());
    
    // poll until delivery data arrives, then load timeline
    this.pollId = window.setInterval(() => {
      const d = this.delivery();
      if (d) {
        this.data.loadTimeline(d.trackingNumber);
        window.clearInterval(this.pollId);
      }
      this.attempts.update(value => value + 1);
      if (this.attempts() > 50) window.clearInterval(this.pollId);
    }, 100);
  }

  // cleanup polling on component destroy
  ngOnDestroy(): void {
    if (this.pollId) window.clearInterval(this.pollId);
  }
}

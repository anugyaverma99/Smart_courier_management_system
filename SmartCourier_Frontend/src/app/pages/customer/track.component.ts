import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { ApiService } from '../../core/services/api.service';
import { DataService } from '../../core/services/data.service';
import { AuthService } from '../../core/services/auth.service';
import { Delivery, TrackingEvent } from '../../core/models';

@Component({
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: './track.component.html',
  styleUrls:['./track.component.css']
})
export class TrackComponent {
  private readonly api = inject(ApiService);
  readonly data = inject(DataService);
  readonly auth = inject(AuthService);
  
  // search input and loading state
  trackingNumber = signal('');
  loading = signal(false); error = signal('');
  delivery = signal<Delivery | null>(null);
  events = signal<TrackingEvent[]>([]);
  
  // get the latest status from tracking events
  latestStatus = computed(() => this.events().at(-1)?.status ?? this.delivery()?.status ?? 'UNKNOWN');
  
  // search delivery by tracking number
  search(): void {
    this.loading.set(true); this.error.set('');
    const tn = this.trackingNumber().trim();
    
    // first get delivery details
    this.api.get<Delivery>(`/deliveries/track/${tn}`).subscribe({
      next: d => {
        this.delivery.set(d);
        this.data.loadTimeline(tn);
        
        // then get tracking events
        this.api.get<TrackingEvent[]>(`/tracking/${tn}`).subscribe({
          next: events => this.events.set(events),
          error: () => this.events.set([])
        });
      },
      error: err => { this.error.set(err?.status === 401 ? 'Please login to view backend-protected tracking data.' : (err?.error?.message ?? 'Tracking number not found')); this.loading.set(false); },
      complete: () => this.loading.set(false)
    });
  }
}

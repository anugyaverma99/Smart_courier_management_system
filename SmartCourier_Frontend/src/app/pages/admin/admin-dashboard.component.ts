import { Component, computed, inject, OnInit } from '@angular/core';
import { DataService } from '../../core/services/data.service';

@Component({
  standalone: true,
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  readonly data = inject(DataService);
  // compute dashboard stats from backend or fallback to local data
  private stats = computed(() => this.data.adminStats());
  total = computed(() => this.stats()?.totalDeliveries ?? this.data.deliveries().length);
  deliveredToday = computed(() => this.stats()?.deliveredToday ?? this.data.deliveries().filter(d => d.status === 'DELIVERED').length);
  inTransit = computed(() => this.stats()?.inTransit ?? this.data.deliveries().filter(d => d.status === 'IN_TRANSIT').length);
  exceptions = computed(() => this.stats()?.exceptions ?? this.data.exceptions().filter(e => e.resolutionStatus !== 'RESOLVED').length);
  activeHubs = computed(() => this.stats()?.activeHubs ?? this.data.hubs().filter(h => h.active).length);
  trackingEvents = computed(() => this.stats()?.totalTrackingEvents ?? 0);
  liveDeliveryCount = computed(() => this.stats()?.liveDeliveryCount ?? this.data.monitor().length);
  userCount = computed(() => this.data.users().length);
  // load all admin data on init
  ngOnInit(): void { this.data.loadAdmin(); }
}

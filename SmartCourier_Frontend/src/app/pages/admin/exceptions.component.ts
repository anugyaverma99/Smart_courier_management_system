import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DataService } from '../../core/services/data.service';
import { PaginationComponent } from '../../shared/components/pagination.component';

@Component({
  standalone: true,
  imports: [FormsModule, PaginationComponent],
  templateUrl: './exceptions.component.html'
})
export class ExceptionsComponent implements OnInit {
  readonly data = inject(DataService);
  // filter controls: search, mode (OPEN/ALL/RESOLVED), page
  search = signal(''); mode = signal<'OPEN'|'ALL'|'RESOLVED'>('OPEN'); page = signal(1);
  // filter exceptions by mode and search text
  filtered = computed(() => this.data.exceptions().filter(e => {
    const modeOk = this.mode() === 'ALL' || (this.mode() === 'OPEN' ? e.resolutionStatus !== 'RESOLVED' : e.resolutionStatus === 'RESOLVED');
    const q = this.search().toLowerCase();
    return modeOk && (!q || `${e.trackingNumber} ${e.reason}`.toLowerCase().includes(q));
  }));
  // paginate filtered results
  pageItems = computed(() => this.filtered().slice((this.page() - 1) * 10, this.page() * 10));
  // load admin data on init
  ngOnInit(): void { this.data.loadAdmin(); }
}

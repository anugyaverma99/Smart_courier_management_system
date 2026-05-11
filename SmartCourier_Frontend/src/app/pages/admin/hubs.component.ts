import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DataService } from '../../core/services/data.service';
import { PaginationComponent } from '../../shared/components/pagination.component';

@Component({
  standalone: true,
  imports: [FormsModule, PaginationComponent],
  templateUrl: './hubs.component.html'
})
export class HubsComponent implements OnInit {
  readonly data = inject(DataService);
  // form fields for creating new hub
  name = signal(''); city = signal(''); state = signal(''); pincode = signal(''); contactNumber = signal(''); search = signal(''); page = signal(1);
  // filter hubs by search text
  filtered = computed(() => this.data.hubs().filter(h => !this.search() || `${h.name} ${h.city} ${h.state}`.toLowerCase().includes(this.search().toLowerCase())));
  // paginate filtered results
  pageItems = computed(() => this.filtered().slice((this.page() - 1) * 10, this.page() * 10));
  // load admin data on init
  ngOnInit(): void { this.data.loadAdmin(); }
  // create a new hub and reset form
  create(): void {
    this.data.createHub({ name: this.name(), city: this.city(), state: this.state(), pincode: this.pincode(), contactNumber: this.contactNumber() });
    this.name.set(''); this.city.set(''); this.state.set(''); this.pincode.set(''); this.contactNumber.set('');
  }
}

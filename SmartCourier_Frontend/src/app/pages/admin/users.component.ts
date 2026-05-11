import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DataService } from '../../core/services/data.service';
import { AuthService } from '../../core/services/auth.service';
import { PaginationComponent } from '../../shared/components/pagination.component';
import { Router } from '@angular/router';
@Component({
  standalone: true,
  imports: [FormsModule, PaginationComponent],
  templateUrl: './users.component.html'
})
export class UsersComponent implements OnInit {
  readonly data = inject(DataService);
   readonly auth = inject(AuthService);
    readonly router = inject(Router);
  // search and role filter
  search = signal(''); role = signal(''); page = signal(1);
  // filter users by role and search text
  filtered = computed(() => this.data.users().filter(u => (!this.role() || u.role === this.role()) && (!this.search() || `${u.fullName} ${u.email}`.toLowerCase().includes(this.search().toLowerCase()))));
  // paginate filtered results
  pageItems = computed(() => this.filtered().slice((this.page() - 1) * 10, this.page() * 10));
  // load admin data on init
  ngOnInit(): void { this.data.loadAdmin(); }
  // navigate to create admin page
  openCreateAdmin(): void {
  this.router.navigateByUrl('/admin/create-admin');
}
}

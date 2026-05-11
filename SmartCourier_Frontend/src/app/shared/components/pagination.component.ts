import { Component, computed, input, output } from '@angular/core';

@Component({
  selector: 'app-pagination',
  standalone: true,
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.css']
})
export class PaginationComponent {
  // inputs from parent
  total = input.required<number>();
  page = input.required<number>();
  pageSize = input(10);
  // emit page change to parent
  pageChange = output<number>();
  // calculate total pages
  pages = computed(() => Math.max(1, Math.ceil(this.total() / this.pageSize())));
  // calculate range of items shown
  start = computed(() => this.total() ? ((this.page() - 1) * this.pageSize()) + 1 : 0);
  end = computed(() => Math.min(this.total(), this.page() * this.pageSize()));
  // show up to 5 page buttons around current page
  visiblePages = computed(() => {
    const max = this.pages();
    const current = this.page();
    const start = Math.max(1, Math.min(current - 2, max - 4));
    return Array.from({ length: Math.min(5, max) }, (_, i) => start + i);
  });
}

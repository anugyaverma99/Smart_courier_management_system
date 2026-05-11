import { AfterViewInit, Component, computed, effect, ElementRef, inject, OnDestroy, OnInit, signal, viewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { Chart } from 'chart.js/auto';
import { DataService } from '../../core/services/data.service';
import { PaginationComponent } from '../../shared/components/pagination.component';
import { ReportItem, ReportType } from '../../core/models';

@Component({
  standalone: true,
  imports: [FormsModule, DatePipe, PaginationComponent],
  templateUrl: './reports.component.html'
})
export class ReportsComponent implements OnInit, AfterViewInit, OnDestroy {
  readonly data = inject(DataService);
  // chart reference and instance
  chartCanvas = viewChild<ElementRef<HTMLCanvasElement>>('chartCanvas');
  private chart: Chart | null = null;
  private viewReady = signal(false);
  // report type and date range
  type = signal<ReportType>('DAILY');
  fromDate = signal(new Date().toISOString().slice(0,10));
  toDate = signal(new Date().toISOString().slice(0,10));
  page = signal(1);
  // filter reports by selected type
  filtered = computed(() => this.data.reports().filter(r => r.reportType === this.type()));
  // get the most recent report for chart
  latest = computed<ReportItem | null>(() => this.filtered()[0] ?? null);
  pageItems = computed(() => this.filtered().slice((this.page() - 1) * 10, this.page() * 10));

  // re-render chart whenever report data changes
  constructor() {
    effect(() => {
      this.latest();
      this.viewReady();
      // What it does: Delays execution until current JS task finishes Runs before next rendering cycle  So chart rendering happens after signals settle
      queueMicrotask(() => this.renderChart());
    });
  }

  // load reports on init
  ngOnInit(): void { this.load(); }
  ngAfterViewInit(): void { this.viewReady.set(true); this.renderChart(); }
  // destroy chart on component cleanup
  ngOnDestroy(): void { this.chart?.destroy(); }
  // fetch reports from backend
  load(): void { this.data.loadReports(this.type()); }
  // generate a new report
  generate(): void { this.data.generateReport(this.type(), this.fromDate(), this.toDate()); }

  // render bar chart with report breakdown
  private renderChart(): void {
    const canvas = this.chartCanvas()?.nativeElement;
    const report = this.latest();
    if (!canvas || !report) return;
    this.chart?.destroy();
    this.chart = new Chart(canvas, {
      type: 'bar',
      data: {
        labels: ['Total', 'Delivered', 'Failed', 'Delayed', 'Returned'],
        datasets: [{ data: [report.totalDeliveries, report.deliveredCount, report.failedCount, report.delayedCount, report.returnedCount], backgroundColor: ['#0d9488', '#16a34a', '#dc2626', '#f97316', '#64748b'], borderRadius: 8 }]
      },

      // Legend = label box (color indicators)
      // precision: 0 means: no decimal values
      // responsive: true  Makes the chart automatically adjust to screen size.

      options: { responsive: true, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true, ticks: { precision: 0 } } } }
    });
  }
  
}

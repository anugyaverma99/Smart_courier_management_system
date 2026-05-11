import { Routes } from '@angular/router';
import { adminGuard } from './core/guards/admin.guard';
import { customerGuard } from './core/guards/customer.guard';

export const routes: Routes = [
  // public routes accessible without login
  { path: '', loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent) },
  { path: 'login', loadComponent: () => import('./pages/auth/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./pages/auth/register.component').then(m => m.RegisterComponent) },
  { path: 'track', loadComponent: () => import('./pages/customer/track.component').then(m => m.TrackComponent) },
  
  // protected customer routes (requires customer role)
  {
    path: 'customer',
    canActivate: [customerGuard],
    loadComponent: () => import('./pages/customer/customer-shell.component').then(m => m.CustomerShellComponent),
    children: [
      { path: '', loadComponent: () => import('./pages/customer/customer-dashboard.component').then(m => m.CustomerDashboardComponent) },
      { path: 'create', loadComponent: () => import('./pages/customer/create-delivery.component').then(m => m.CreateDeliveryComponent) },
      { path: 'delivery/:id', loadComponent: () => import('./pages/customer/delivery-detail.component').then(m => m.DeliveryDetailComponent) },
      { path: 'upload', loadComponent: () => import('./pages/customer/upload-documents.component').then(m => m.UploadDocumentsComponent) }
    ]
  },
  
  // protected admin routes (requires admin role)
  {
    path: 'admin',
    canActivate: [adminGuard],
    loadComponent: () => import('./pages/admin/admin-shell.component').then(m => m.AdminShellComponent),
    children: [
      { path: '', loadComponent: () => import('./pages/admin/admin-dashboard.component').then(m => m.AdminDashboardComponent) },
      { path: 'monitor', loadComponent: () => import('./pages/admin/monitor.component').then(m => m.MonitorComponent) },
      { path: 'proof', loadComponent: () => import('./pages/admin/delivery-proof.component').then(m => m.DeliveryProofComponent) },
      { path: 'exceptions', loadComponent: () => import('./pages/admin/exceptions.component').then(m => m.ExceptionsComponent) },
      { path: 'hubs', loadComponent: () => import('./pages/admin/hubs.component').then(m => m.HubsComponent) },
      { path: 'reports', loadComponent: () => import('./pages/admin/reports.component').then(m => m.ReportsComponent) },
      { path: 'users', loadComponent: () => import('./pages/admin/users.component').then(m => m.UsersComponent) },
      {
        path: 'create-admin',
        loadComponent: () =>
          import('./pages/admin/create-admin.component').then(m => m.CreateAdminComponent)
      }
    ]
  },
  
  // catch-all redirect to home
  { path: '**', redirectTo: '' }
];

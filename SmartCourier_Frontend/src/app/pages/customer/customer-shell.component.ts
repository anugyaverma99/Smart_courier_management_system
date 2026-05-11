import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../../shared/components/sidebar.component';

@Component({
  standalone: true,
  imports: [RouterOutlet, SidebarComponent],
  templateUrl: './customer-shell.component.html'
})
export class CustomerShellComponent {
  // sidebar navigation links for customer panel
  links = [
    { label: 'Dashboard', href: '/customer', exact: true, icon: 'DB', group: 'MAIN' },
    { label: 'Create Delivery', href: '/customer/create', icon: 'CR', group: 'TOOLS' },
    { label: 'Upload Documents', href: '/customer/upload', icon: 'UP', group: 'TOOLS' },
    { label: 'Track Delivery', href: '/track', icon: 'TR', group: 'ACCOUNT' }
  ];
}

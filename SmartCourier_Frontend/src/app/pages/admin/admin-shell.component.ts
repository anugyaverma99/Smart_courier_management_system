import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../../shared/components/sidebar.component';

@Component({
  standalone: true,
  imports: [RouterOutlet, SidebarComponent],
  templateUrl: './admin-shell.component.html'
})
export class AdminShellComponent {
  // sidebar navigation links for admin panel
  links = [
    { label: 'Dashboard', href: '/admin', exact: true, icon: 'DB', group: 'MAIN' },
    { label: 'Monitor', href: '/admin/monitor', icon: 'OP', group: 'MAIN' },
    { label: 'Delivery Proof', href: '/admin/proof', icon: 'POD', group: 'MAIN' },
    { label: 'Exceptions', href: '/admin/exceptions', icon: 'EX', group: 'TOOLS' },
    { label: 'Hubs', href: '/admin/hubs', icon: 'HB', group: 'TOOLS' },
    { label: 'Reports', href: '/admin/reports', icon: 'RP', group: 'TOOLS' },
    { label: 'Users', href: '/admin/users', icon: 'US', group: 'ACCOUNT' }
  ];
}

import { Component, computed, inject, input } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrl:'./sidebar.component.css'
})
export class SidebarComponent {
  readonly auth = inject(AuthService);
  // receive navigation links from parent component
  links = input.required<Array<{ label: string; href: string; exact?: boolean; icon?: string; group?: string }>>();
  // group links by category (MAIN, TOOLS, ACCOUNT)
  groupedLinks = computed(() => {
    const groups = ['MAIN', 'TOOLS', 'ACCOUNT'];
    return groups.map(label => ({ label, links: this.links().filter(link => (link.group ?? 'MAIN') === label) })).filter(group => group.links.length);
  });
  // get user initials for avatar
  initials = computed(() => (this.auth.user()?.fullName ?? 'FD').split(' ').map(part => part[0]).join('').slice(0, 2).toUpperCase());
}

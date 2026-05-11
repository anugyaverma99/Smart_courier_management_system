import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrls:['./app.component.css']
})
export class AppComponent {
  // inject auth service for navbar state
  readonly auth = inject(AuthService);
  // display user name and role in navbar
  readonly identity = computed(() => `${this.auth.user()?.fullName ?? 'User'} · ${this.auth.user()?.role ?? ''}`);
}

import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  standalone: true,
  imports: [FormsModule],
  templateUrl: './create-admin.component.html'
})
export class CreateAdminComponent {

  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  fullName = '';
  email = '';
  phone = '';
  password = '';

  loading = signal(false);
  error = signal('');
  success = signal('');

  // submit admin creation form
  createAdmin(): void {
    // start loading
    this.loading.set(true);
    this.error.set('');
    this.success.set('');

    // call backend to create admin account
    this.auth.createAdmin({
      fullName: this.fullName,
      email: this.email,
      phone: this.phone,
      password: this.password
    }).subscribe({
      // show success message
      next: () => {
        this.success.set('Admin created successfully');
        this.loading.set(false);
      },
      // show error message
      error: (err) => {
        this.error.set(err?.error?.message || 'Failed to create admin');
        this.loading.set(false);
      }
    });
  }
}
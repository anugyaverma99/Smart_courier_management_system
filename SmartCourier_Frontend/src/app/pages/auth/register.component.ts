import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './auth.css'
})
export class RegisterComponent {

  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  fullName = '';
  email = '';
  phone = '';
  password = '';

  loading = signal(false);
  error = signal('');

  submit(): void {

    // check all fields are filled
    if (!this.fullName || !this.email || !this.phone || !this.password) {
      this.error.set('All fields are required');
      return;
    }

    // validate password length
    if (this.password.length < 6) {
      this.error.set('Password must be at least 6 characters');
      return;
    }

    // validate phone format
    if (!/^[0-9]{10}$/.test(this.phone)) {
      this.error.set('Phone must be 10 digits');
      return;
    }

    // start loading
    this.loading.set(true);
    this.error.set('');

    // call backend signup api
    this.auth.signup({
      fullName: this.fullName,
      email: this.email,
      phone: this.phone,
      password: this.password
    }).subscribe({

      next: (user: any) => {

        // redirect based on role
        if (user.role === 'ADMIN') {
          this.router.navigateByUrl('/admin');
        } else {
          this.router.navigateByUrl('/customer');
        }

      },

      // handle signup failure
      error: (err) => {
        this.error.set(err?.error?.message || 'Registration failed');
        this.loading.set(false);
      },

      // stop loading spinner
      complete: () => {
        this.loading.set(false);
      }
    });
  }
}
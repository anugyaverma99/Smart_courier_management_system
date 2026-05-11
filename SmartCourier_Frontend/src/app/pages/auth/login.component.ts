import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './auth.css'
})
export class LoginComponent {

  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  email = '';
  password = '';

  loading = signal(false);
  error = signal('');

  submit(): void {
    
    // check if inputs are empty
    if (!this.email || !this.password) {
      this.error.set('Email and password are required');
      return;
    }

    // start loading spinner
    this.loading.set(true);
    this.error.set('');

    // call backend api
    this.auth.login(this.email, this.password).subscribe({
      next: (user: any) => {

        // clear error on success
        this.error.set('');

        // route based on user role
        if (user.role === 'ADMIN') {
          this.router.navigateByUrl('/admin');
        } else {
          this.router.navigateByUrl('/customer');
        }
      },

      // handle login failure
      error: (err) => {
        const backendMessage =
          err?.error?.message ||
          err?.error?.error ||
          'Invalid email or password';

        // show error message on screen
        this.error.set(backendMessage);
        this.loading.set(false);
      },

      // always stop loading spinner
      complete: () => {
        this.loading.set(false);
      }
    });
  }
}
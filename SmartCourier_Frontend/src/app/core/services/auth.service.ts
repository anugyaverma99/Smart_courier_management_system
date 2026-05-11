import { computed, effect, inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { ApiService } from './api.service';
import { AuthUser, Role } from '../models';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  private readonly key = 'smartcourier.session';
  readonly user = signal<AuthUser | null>(this.restore());
  readonly isLoggedIn = computed(() => !!this.user()?.token && this.user()?.active !== false);
  readonly role = computed<Role | null>(() => this.user()?.role ?? null);
  readonly isAdmin = computed(() => this.isLoggedIn() && this.role() === 'ADMIN');
  readonly isCustomer = computed(() => this.isLoggedIn() && this.role() === 'CUSTOMER');

  constructor() {
    // save user session automatically when it changes
    effect(() => {
      const user = this.user();
      if (user) localStorage.setItem(this.key, JSON.stringify(user));
      else localStorage.removeItem(this.key);
    });
  }

  // send login credentials to backend
  login(email: string, password: string) {
    return this.api.post<AuthUser>('/auth/login', { email, password }).pipe(tap((user) => this.user.set({ ...user, active: user.active ?? true })));
  }
  
  // send signup details to backend
  signup(input: { fullName: string; email: string; password: string; phone: string }) {
    return this.api.post<AuthUser>('/auth/signup', input).pipe(tap((user) => this.user.set({ ...user, active: user.active ?? true })));
  }
  
  // clear session and redirect
  logout(): void {
    this.user.set(null);
    void this.router.navigateByUrl('/login');
  }
  
  // set user status as inactive
  markInactive(userId: number): void {
    const current = this.user();
    if (current?.userId === userId) this.user.set({ ...current, active: false });
  }
  private restore(): AuthUser | null { //prevents logout after refresh
    try {
      const raw = localStorage.getItem(this.key);
      return raw ? JSON.parse(raw) as AuthUser : null;
    } catch {
      return null;
    }
  }
  createAdmin(input: {
  fullName: string;
  email: string;
  phone: string;
  password: string;
}) {
  const token = this.user()?.token;
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });
  return this.http.post(
    'http://localhost:8085/auth/admin/create',
    input,
    { headers }
  );
}
}

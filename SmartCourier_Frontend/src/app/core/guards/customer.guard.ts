import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const customerGuard: CanActivateFn = () => {
  // inject necessary services
  const auth = inject(AuthService);
  const router = inject(Router);
  
  // allow access if user is a customer
  if (auth.isCustomer()) return true;
  
  // redirect admins to admin panel, otherwise to login
  return router.createUrlTree([auth.isAdmin() ? '/admin' : '/login']);
};

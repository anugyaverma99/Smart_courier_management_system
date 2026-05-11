import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = () => {
  // inject auth and router services
  const auth = inject(AuthService);
  const router = inject(Router);
  
  // allow access if user is an admin
  if (auth.isAdmin()) return true;
  
  // redirect customers to customer portal, otherwise to login
  return router.createUrlTree([auth.isCustomer() ? '/customer' : '/login']);
};

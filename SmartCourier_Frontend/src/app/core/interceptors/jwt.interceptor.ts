import { HttpInterceptorFn } from '@angular/common/http';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  // get user session from local storage
  const raw = localStorage.getItem('smartcourier.session');
  // Converts string into object.
  const user = raw ? JSON.parse(raw) as { token?: string; email?: string; role?: string } : null;
  
  const headers: Record<string, string> = {};
  
  // add auth token
  // If token exists
  if (user?.token) headers['Authorization'] = `Bearer ${user.token}`;
  
  // add user details for logging
  if (user?.email) headers['X-User-Email'] = user.email;
  if (user?.role) headers['X-User-Role'] = user.role;
  
  // send the request
  return next(Object.keys(headers).length ? req.clone({ setHeaders: headers }) : req);
};

import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';
import { jwtInterceptor } from './app/core/interceptors/jwt.interceptor';

// bootstrap the angular app
// bootstrapApplication()--directly starts app without AppModule.
bootstrapApplication(AppComponent, {
  providers: [
    // register routes with input binding for route params
    provideRouter(routes, withComponentInputBinding()),
    // register http client with jwt interceptor
    provideHttpClient(withInterceptors([jwtInterceptor]))
  ]
}).catch((err) => console.error(err));

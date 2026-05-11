import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  readonly baseUrl = 'http://localhost:8080/gateway';

  // standard GET request
  // Example
// this.api.get('/deliveries', { page:1 })

// becomes:

//http://localhost:8080/gateway/deliveries?page=1


  get<T>(path: string, params?: Record<string, string | number | boolean>): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${path}`, { params: this.params(params) });
  }
  
  // standard POST request
  post<T>(path: string, body: unknown, params?: Record<string, string | number | boolean>): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${path}`, body, { params: this.params(params) });
  }
  
  // standard PUT request
  put<T>(path: string, body: unknown, params?: Record<string, string | number | boolean>): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}${path}`, body, { params: this.params(params) });
  }
  
  // standard DELETE request
  delete<T>(path: string): Observable<T> {
    return this.http.delete<T>(`${this.baseUrl}${path}`);
  }
  
  // handle file uploads via FormData
  upload<T>(path: string, form: FormData): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${path}`, form);
  }
  
  // fetch binary files like PDFs
  blob(path: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}${path}`, { responseType: 'blob' });
  }
  
  // utility to parse query parameters
  private params(params?: Record<string, string | number | boolean>): HttpParams {
    let out = new HttpParams();
    Object.entries(params ?? {}).forEach(([key, value]) => out = out.set(key, String(value)));
    return out;
  }
}

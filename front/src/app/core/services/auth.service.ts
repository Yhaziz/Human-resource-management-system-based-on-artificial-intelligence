import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';

const BASE_URL = ['http://localhost:8081/']

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  http = inject(HttpClient);
  router = inject(Router);

  currentUserSig = signal<any | undefined | null>(undefined);


  login(loginRequest: any): Observable<any> {
    return this.http.post(BASE_URL + "auth/login", loginRequest)
  }
  logOut(): any {
     return this.http.get(BASE_URL + 'logout');
  }
  cleanData(): void {
    window.localStorage.clear();
  }
}

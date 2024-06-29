import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { inject } from '@angular/core';

export const authInterceptor: HttpInterceptorFn = (request, next) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  const token = localStorage.getItem('jwt') ?? '';
  request = request.clone({
    setHeaders: {
      Authorization: token ? `Bearer ${token}` : '',
    },
  });

  return next(request).pipe(
    catchError((e: HttpErrorResponse)=>{
      if(e.status === 401){
        authService.cleanData();
        router.navigateByUrl('/login');

      }
      const error = e.error?.message || e.statusText;
      return throwError(() => error);
    })
  );
};

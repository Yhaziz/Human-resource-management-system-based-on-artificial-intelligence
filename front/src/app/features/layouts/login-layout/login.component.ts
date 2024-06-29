
import { Component, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { NgxSonnerToaster, toast } from 'ngx-sonner';



const usernameValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const value = control.value;

  if (!value) {
    return null;
  }

  if (value.length !== 8) {
    return { invalidLength: true };
  }



  return null;
};

const passwordValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const value = control.value;

  if (!value) {
    return null;
  }

  if (value.length < 8 ) {
    return { invalidPassword: true };
  }

  return null;
};


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, NgxSonnerToaster],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  fb = inject(FormBuilder);
  http = inject(HttpClient);
  authService = inject(AuthService);
  router = inject(Router);
  protected readonly toast = toast;

  loginForm: FormGroup;

  constructor() { }





  ngOnInit() {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, usernameValidator]],
      password: ['', [Validators.required, passwordValidator]],
    })
    if ((localStorage.getItem('jwt') && localStorage.getItem('role'))){
      const role = localStorage.getItem('role');
      if(role == 'DG'){
        this.router.navigateByUrl('/dg');
        
      }else if(role == 'RRH'){
        this.router.navigateByUrl('/rrh');
      }else if(role == 'CD') {
        this.router.navigateByUrl('/cd');
      }else {
        this.router.navigateByUrl('/employee');
      }
    }

  }


  login() {
    setTimeout(() => {
      this.toast.loading('Chargement...');
    }, 500);
    this.authService.login(this.loginForm.value).subscribe(
      (response) => {
        const jwtToken = response.accessToken;
        const role = response.loggedRole;
        localStorage.setItem('jwt', jwtToken);
        localStorage.setItem('role', role);
  
        if (role === 'DG') {
          setTimeout(() => {
            this.toast('Login avec succès');
            setTimeout(() => {
              this.router.navigateByUrl('/dg');
            }, 1000);
          }, 1000);
        } else if (role === 'RRH') {
          setTimeout(() => {
            this.toast('Login avec succès');
            setTimeout(() => {
              this.router.navigateByUrl('/rrh');
            }, 1000);
          }, 1000);
          
        } else if (role === 'CD') {
          setTimeout(() => {
            this.toast('Login avec succès');
            setTimeout(() => {
              this.router.navigateByUrl('/cd');
            }, 1000);
          }, 1000);
        } else if (role === 'USER') {
          this.router.navigateByUrl('/employee');
          setTimeout(() => {
            this.toast('Login avec succès');
            setTimeout(() => {
              
            }, 1000);
          }, 1000);
        }
      },
      (error) => {
        this.toast.error('Incorrect credentials.');
        console.log("error ziz :", error)
        this.loginForm.setErrors({ 'invalid': true });
      });
  }
  


  showPassword = false;

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
    const passwordInput = document.querySelector('input[name="password"]') as HTMLInputElement;
    if (passwordInput) {
      passwordInput.type = this.showPassword ? 'text' : 'password';
    }
  }



}

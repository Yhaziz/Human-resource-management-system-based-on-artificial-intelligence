import { Component, inject } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { toast, NgxSonnerToaster } from 'ngx-sonner';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NgxSonnerToaster],
  providers: [],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'SMARTInsight';
  authService = inject(AuthService);
  router = inject(Router);

  ngOnInit() {
    if (!(localStorage.getItem('jwt') && localStorage.getItem('role'))) {
      this.authService.cleanData();
      this.router.navigateByUrl('/login');
    }
  }
}

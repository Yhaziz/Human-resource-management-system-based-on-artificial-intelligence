import { Routes } from '@angular/router';

export const EMPLOYEE_ROUTES: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    loadComponent: () => import('../../components/employee/dashboard/dashboard.component').then((c) => c.DashboardComponent)
  },
  {
    path: 'mes-demandes',
    loadComponent: () => import('../../components/shared/mesdemandes/mesdemandes.component').then((c) => c.MesdemandesComponent),
  },
  {
    path: 'mes-demandes/ajouter-demande',
    loadComponent: () => import('../../components/shared/ajouter-demande/ajouter-demande.component').then((c) => c.AjouterDemandeComponent)
  },
  {
    path: 'notifications',
    loadComponent: () => import('../../components/shared/notification/notification.component').then((c) => c.NotificationComponent)
  },


];

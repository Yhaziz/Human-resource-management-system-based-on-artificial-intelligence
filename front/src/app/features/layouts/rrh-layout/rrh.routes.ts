import { Routes } from '@angular/router';

export const RRH_ROUTES: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    loadComponent: () => import('../../components/rrh/dashboard/dashboard.component').then((c) => c.DashboardComponent)
  },
  {
    path: 'departements',
    loadComponent: () => import('../../components/shared/departements/departements.component').then((c) => c.DepartementsComponent)
  },
  {
    path: 'employes',
    loadComponent: () => import('../../components/shared/employes/employes.component').then((c) => c.EmployesComponent)
  },
  {
    path: 'demandes',
    loadComponent: () => import('../../components/shared/demandes/demandes.component').then((c) => c.DemandesComponent)
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

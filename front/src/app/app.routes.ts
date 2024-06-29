
import { Routes } from '@angular/router';



export const routes: Routes = [

  {
    path: '',
    redirectTo:'login',
    pathMatch: 'full'
  },
  {
        path: 'login',
        loadComponent: () =>
            import('./features/layouts/login-layout/login.component').then(
                (c) => c.LoginComponent
            )
    },

    {
        path: 'dg',
        loadComponent: () =>
            import('./features/layouts/dg-layout/dg-layout.component').then(
                (r) => r.DgLayoutComponent
            ),
        loadChildren: () =>
            import('./features/layouts/dg-layout/dg.routes').then(
                (r) => r.DG_ROUTES
            )
    },

    {
      path: 'rrh',
      loadComponent: () =>
          import('./features/layouts/rrh-layout/rrh-layout.component').then(
              (r) => r.RrhLayoutComponent
          ),
      loadChildren: () =>
          import('./features/layouts/rrh-layout/rrh.routes').then(
              (r) => r.RRH_ROUTES
          )
    },

    {
      path: 'cd',
      loadComponent: () =>
          import('./features/layouts/cd-layout/cd-layout.component').then(
              (r) => r.CdLayoutComponent
          ),
      loadChildren: () =>
          import('./features/layouts/cd-layout/cd.routes').then(
              (r) => r.CD_ROUTES
          )
    },


    {
      path: 'employee',
      loadComponent: () =>
          import('./features/layouts/employee-layout/employee-layout.component').then(
              (r) => r.EmployeeLayoutComponent
          ),
      loadChildren: () =>
          import('./features/layouts/employee-layout/employee.routes').then(
              (r) => r.EMPLOYEE_ROUTES
          )
    },

];



import { Demande, User } from './../../../../core/types/index';
import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BrnDialogContentDirective, BrnDialogTriggerDirective } from '@spartan-ng/ui-dialog-brain';
import {
  HlmDialogComponent,
  HlmDialogContentComponent,
  HlmDialogDescriptionDirective,
  HlmDialogFooterComponent,
  HlmDialogHeaderComponent,
  HlmDialogTitleDirective,
} from '@spartan-ng/ui-dialog-helm';

import { HlmIconComponent } from '@spartan-ng/ui-icon-helm';
import { provideIcons } from '@ng-icons/core';
import { lucideEye } from '@ng-icons/lucide'
import { HlmButtonDirective, HlmButtonModule } from '@spartan-ng/ui-button-helm';


import { HlmBadgeDirective } from '@spartan-ng/ui-badge-helm';
import { DgService } from '../../../../core/services/dg.service';
import { DateTime } from 'luxon';

import { NgxSonnerToaster, toast } from 'ngx-sonner';


@Component({
  selector: 'app-mesdemandes',
  standalone: true,
  imports: [
    BrnDialogContentDirective,
    BrnDialogTriggerDirective,

    HlmButtonDirective,
    HlmButtonModule,
    HlmBadgeDirective,
    RouterModule,
    HlmDialogComponent,
    HlmDialogContentComponent,
    HlmDialogDescriptionDirective,
    HlmDialogFooterComponent,
    HlmDialogHeaderComponent,
    HlmDialogTitleDirective,
    HlmIconComponent,
    NgxSonnerToaster,

  ],
  providers: [provideIcons({ lucideEye })],

  templateUrl: './mesdemandes.component.html',
  styleUrl: './mesdemandes.component.css'
})
export class MesdemandesComponent {
  demandesPending: Demande[];
  demandesDecided: Demande[];
  currentUser: User;
  selectedDemande: Demande;
  protected readonly toast = toast;


  constructor(private dgService: DgService) {


  }

  ngOnInit() {
    this.dgService.dashboardBootstrap()
    this.demandesPending = this.dgService.currentUserData().demandes.pending;
    this.demandesDecided = this.dgService.currentUserData().demandes.decided;
    this.currentUser = this.dgService.currentUserData().user;

  }
  async refreshData(){
    await this.dgService.dashboardBootstrap()
    this.demandesPending = this.dgService.currentUserData().demandes.pending;
    this.demandesDecided = this.dgService.currentUserData().demandes.decided;
    this.currentUser = this.dgService.currentUserData().user;
  }
  returnType(type: string, cat: any): string {
    if (type === 'CONGE') {
      return `Congé (${cat})`;
    } else if (type === 'SORTIE') {
      return "Autorisation de sortie";
    } else {
      return "Explication d'un retard";
    }
  }

  returnDate(type: any, debut: any, fin: any): string {
    if (type === 'CONGE') {
      if (debut !== fin) {
        return `${debut.substring(0, 10)} - ${fin.substring(0, 10)}`;
      } else {
        return `${debut.substring(0, 10)}`;
      }
    } else {
      return `${debut.substring(0, 10)} | ${debut.substring(11, 16)} - ${fin.substring(11, 16)}`;
    }
  }

  returnDuree(type: any, startDateTime: any, endDateTime: any): string {
    const start = DateTime.fromISO(startDateTime);
    const end = DateTime.fromISO(endDateTime);

    const duration = end.diff(start);

    if (type === 'CONGE') {
      const days = Math.floor(duration.as('days'));
      return `${days} ${days === 1 ? 'jour' : 'jours'}`;
    } else if (type === 'SORTIE' || type === 'RETARD') {
      const minutes = Math.floor(duration.as('minutes'));
      if (minutes < 60) {
        return `${minutes} ${minutes === 1 ? 'minute' : 'minutes'}`;
      } else {
        const hours = Math.floor(minutes / 60);
        const remainingMinutes = minutes % 60;
        if (remainingMinutes === 0) {
          return `${hours} ${hours === 1 ? 'heure' : 'heures'}`;
        } else {
          return `${hours} ${hours === 1 ? 'heure' : 'heures'}, ${remainingMinutes} ${remainingMinutes === 1 ? 'minute' : 'minutes'}`;
        }
      }
    } else {
      return 'Type de durée invalide';
    }
  }

  selectDemande(demande: Demande) {
    this.selectedDemande = demande;
  }
  async cancelDemande(ctx){
      if(this.dgService.cancelDemande(this.selectedDemande.id)){
        this.toast('Votre demande a été annulée.');
        await this.refreshData();
        ctx.close();
      }
    }

}

import { Demande, User } from './../../../../core/types/index';
import { Component } from '@angular/core';
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
import { lucideChevronRight, lucideX, lucideCheck, lucideBrainCircuit } from '@ng-icons/lucide';
import { HlmButtonDirective, HlmButtonModule } from '@spartan-ng/ui-button-helm';

import { HlmAvatarImageDirective, HlmAvatarComponent, HlmAvatarFallbackDirective } from '@spartan-ng/ui-avatar-helm';


import { HlmBadgeDirective } from '@spartan-ng/ui-badge-helm';
import { DgService } from '../../../../core/services/dg.service';
import { DateTime } from 'luxon';




import {
  BrnAlertDialogContentDirective,
  BrnAlertDialogTriggerDirective,
} from '@spartan-ng/ui-alertdialog-brain';
import {
  HlmAlertDialogActionButtonDirective,
  HlmAlertDialogCancelButtonDirective,
  HlmAlertDialogComponent,
  HlmAlertDialogContentComponent,
  HlmAlertDialogDescriptionDirective,
  HlmAlertDialogFooterComponent,
  HlmAlertDialogHeaderComponent,
  HlmAlertDialogOverlayDirective,
  HlmAlertDialogTitleDirective,
} from '@spartan-ng/ui-alertdialog-helm';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';



@Component({
  selector: 'app-demandes',
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



    BrnAlertDialogContentDirective,
    BrnAlertDialogTriggerDirective,


    HlmAlertDialogActionButtonDirective,
    HlmAlertDialogCancelButtonDirective,
    HlmAlertDialogComponent,
    HlmAlertDialogContentComponent,
    HlmAlertDialogDescriptionDirective,
    HlmAlertDialogFooterComponent,
    HlmAlertDialogHeaderComponent,
    HlmAlertDialogOverlayDirective,
    HlmAlertDialogTitleDirective,

    HlmAvatarImageDirective, HlmAvatarComponent, HlmAvatarFallbackDirective,

    ReactiveFormsModule
  ],
  providers: [provideIcons({ lucideChevronRight, lucideX, lucideCheck,lucideBrainCircuit })],
  templateUrl: './demandes.component.html',
  styleUrl: './demandes.component.css'
})
export class DemandesComponent {
  demandesPending: Demande[];
  demandesDecided: Demande[];
  currentUser: User;
  users: User[];
  selectedDemande: Demande;

  reponseForm: FormGroup;


  constructor(private dgService: DgService, private fb: FormBuilder) { }

  ngOnInit() {
    this.dgService.dashboardBootstrap()
    this.demandesPending = this.dgService.dataDemandes().toDecided;
    this.demandesDecided = this.dgService.dataDemandes().decided.all;
    this.currentUser = this.dgService.currentUserData().user;
    if (this.currentUser.role == 'DG' || this.currentUser.role == 'RRH') {
      this.users = this.dgService.dataUsers();
    } else {
      this.users = this.dgService.currentUserData().team.members;
    }

    this.reponseForm = this.fb.group({
      note: ['', Validators.required],
    });
  }

  refuserDemande(ctx: any){
    const note : string = this.reponseForm.value.note;
    if (this.dgService.rejectDemande(this.selectedDemande.id, this.reponseForm.value.note)) {
      //toast

      ctx.close();
      window.location.reload();

    }
  }

  accepterDemande(ctx: any){
    if (this.dgService.acceptDemande(this.selectedDemande.id)) {
      //toast
      ctx.close();
    }
  }



  returnUserImg(id: number): string {
    const user = this.users.find(u => u.id === id)
    return user.imgPath;
  }
  returnUserNom(id: number): string {
    const user = this.users.find(u => u.id === id)
    return user.nomComplet;
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
    console.log("this.selectedDemande", this.selectedDemande);
  }



}

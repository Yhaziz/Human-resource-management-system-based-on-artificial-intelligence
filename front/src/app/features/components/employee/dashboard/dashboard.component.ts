import { DateTime } from 'luxon';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { provideIcons } from '@ng-icons/core';
import {
  lucideTrash2,
  lucideChevronLeft,
  lucideChevronRight,
  lucideTimerReset,
  lucideCalendarClock,
  lucideCalendarPlus,
} from '@ng-icons/lucide';
import {
  HlmCardContentDirective,
  HlmCardDescriptionDirective,
  HlmCardDirective,
  HlmCardFooterDirective,
  HlmCardHeaderDirective,
  HlmCardTitleDirective,
} from '@spartan-ng/ui-card-helm';
import { HlmButtonDirective } from '@spartan-ng/ui-button-helm';
import { HlmIconComponent } from '@spartan-ng/ui-icon-helm';
import { HlmInputDirective } from '@spartan-ng/ui-input-helm';
import { HlmLabelDirective } from '@spartan-ng/ui-label-helm';
import { BrnSelectImports } from '@spartan-ng/ui-select-brain';
import { HlmSelectImports } from '@spartan-ng/ui-select-helm';
import {
  HlmAvatarImageDirective,
  HlmAvatarComponent,
  HlmAvatarFallbackDirective,
} from '@spartan-ng/ui-avatar-helm';

import { DgService } from '../../../../core/services/dg.service';
import { CurrentUserData } from '../../../../core/types';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule,
    HlmCardContentDirective,
    HlmCardDescriptionDirective,
    HlmCardDirective,
    HlmCardFooterDirective,
    HlmCardHeaderDirective,
    HlmCardTitleDirective,
    HlmButtonDirective,
    HlmIconComponent,
    HlmInputDirective,
    HlmLabelDirective,
    BrnSelectImports,
    HlmSelectImports,
    HlmAvatarImageDirective,
    HlmAvatarComponent,
    HlmAvatarFallbackDirective,
  ],
  providers: [
    provideIcons({
      lucideTrash2,
      lucideChevronRight,
      lucideChevronLeft,
      lucideTimerReset,
      lucideCalendarClock,
      lucideCalendarPlus,
    }),
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  currentUser: CurrentUserData;

  constructor(private fb: FormBuilder, private dgService: DgService) {
    this.currentUser = this.dgService.currentUserData();
  }

  returnUserImg(id: number): string {
    const user = this.currentUser.team.members.find((u) => u.id === id);
    return user.imgPath;
  }
  returnUserNom(id: number): string {
    const user = this.currentUser.team.members.find((u) => u.id === id);
    return user.nomComplet;
  }

  returnType(type: string, cat: any): string {
    if (type === 'CONGE') {
      return `Congé`;
    } else if (type === 'SORTIE') {
      return 'Autorisation de sortie';
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
      return `${debut.substring(0, 10)} | ${debut.substring(
        11,
        16
      )} - ${fin.substring(11, 16)}`;
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
          return `${hours} ${
            hours === 1 ? 'heure' : 'heures'
          }, ${remainingMinutes} ${
            remainingMinutes === 1 ? 'minute' : 'minutes'
          }`;
        }
      }
    } else {
      return 'Type de durée invalide';
    }
  }
}

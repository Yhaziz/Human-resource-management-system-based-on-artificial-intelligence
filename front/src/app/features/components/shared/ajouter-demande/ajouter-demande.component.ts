import { DateTime } from 'luxon';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { provideIcons } from '@ng-icons/core';
import { lucideTrash2, lucideChevronLeft, lucideChevronRight, lucideTimerReset, lucideCalendarClock, lucideCalendarPlus } from '@ng-icons/lucide';
import { HlmCardContentDirective, HlmCardDescriptionDirective, HlmCardDirective, HlmCardFooterDirective, HlmCardHeaderDirective, HlmCardTitleDirective } from '@spartan-ng/ui-card-helm';
import { HlmButtonDirective } from '@spartan-ng/ui-button-helm';
import { HlmIconComponent } from '@spartan-ng/ui-icon-helm';
import { HlmInputDirective } from '@spartan-ng/ui-input-helm';
import { HlmLabelDirective } from '@spartan-ng/ui-label-helm';
import { BrnSelectImports } from '@spartan-ng/ui-select-brain';
import { HlmSelectImports } from '@spartan-ng/ui-select-helm';
import { HlmAvatarImageDirective, HlmAvatarComponent, HlmAvatarFallbackDirective } from '@spartan-ng/ui-avatar-helm';

import { DgService } from '../../../../core/services/dg.service';
import { CurrentUserData } from '../../../../core/types';
import { NgxSonnerToaster, toast } from 'ngx-sonner';

@Component({
  selector: 'app-ajouter-demande',
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
    HlmAvatarImageDirective, HlmAvatarComponent, HlmAvatarFallbackDirective,
    NgxSonnerToaster,
  ],
  providers: [provideIcons({ lucideTrash2, lucideChevronRight, lucideChevronLeft, lucideTimerReset, lucideCalendarClock, lucideCalendarPlus })],
  templateUrl: './ajouter-demande.component.html',
  styleUrls: ['./ajouter-demande.component.css']
})
export class AjouterDemandeComponent implements OnInit {
  currentUser: CurrentUserData;
  congeForm: FormGroup;
  sortieForm: FormGroup;
  retardForm: FormGroup;
  motifForm: FormGroup;
  selectedFormType: string | null = null;
  oldFormType: string | null = null;
  files: File[] = [];
  success: boolean = false;
  protected readonly toast = toast;

  constructor(private fb: FormBuilder, private dgService: DgService) { this.currentUser = this.dgService.currentUserData(); }

  ngOnInit() {
    this.congeForm = this.fb.group({
      category: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
    });
    this.sortieForm = this.fb.group({
      sortieDate: ['', Validators.required],
      leavingTime: ['', Validators.required],
      comingTime: ['', Validators.required],
    });
    this.retardForm = this.fb.group({
      retardDate: ['', Validators.required],
      supposedTime: ['', Validators.required],
      actualTime: ['', Validators.required],
    });
    this.motifForm = this.fb.group({
      motif: ['']
    });
  }

  selectForm(formType: string): void {
    this.selectedFormType = formType;
  }


  minDate(): string {
    const today = new Date();
    const year = today.getFullYear();
    const month = (today.getMonth() + 1).toString().padStart(2, '0');
    const day = today.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  }


  nextFormType(): void {
    if (this.selectedFormType === 'motif') {
      this.submitForm();

    } else {
      this.oldFormType = this.selectedFormType;
      this.selectedFormType = 'motif';
    }

  }
  previousFormType(): void {
    this.selectedFormType = this.oldFormType;
    this.oldFormType = null
    if (this.selectedFormType == null) {
      this.files = [];
      this.motifForm.reset();
    }
  }

  submitForm(): void {
    const formData = new FormData();
    let data = {};
    if (this.oldFormType === 'conge') {
      data = {
        type: "CONGE",
        categorie: this.congeForm.value.category,
        debut: this.congeForm.value.startDate + "T00:00:00.000",
        fin: this.congeForm.value.endDate + "T00:00:00.000",
        motif: this.motifForm.value.motif,
      }
    }
    if(this.oldFormType === 'sortie'){
      data = {
        type: "SORTIE",
        categorie: "AUCUN",
        debut: this.sortieForm.value.sortieDate + "T" + this.sortieForm.value.leavingTime + ":00.000",
        fin: this.sortieForm.value.sortieDate + "T" + this.sortieForm.value.comingTime + ":00.000",
        motif: this.motifForm.value.motif,
      }

    }
    if(this.oldFormType === 'retard'){
      data = {
        type: "RETARD",
        categorie: "AUCUN",
        debut: this.retardForm.value.retardDate + "T" + this.retardForm.value.supposedTime + ":00.000",
        fin: this.retardForm.value.retardDate + "T" + this.retardForm.value.actualTime + ":00.000",
        motif: this.motifForm.value.motif,
      }
    }

    formData.append("data", JSON.stringify(data));
    if (this.files.length === 0) {
      console.log("No Files Selected.")
    } else {
      this.files.forEach((file, index) => {
        formData.append(`files`, file);
      });
    }
    if(this.dgService.makeDemande(formData, this.files)){
      this.success = true;
      console.log(data);
      this.resetForms();
    }else{
      this.success = false;
    }

  }

  resetForms(): void {
    this.retardForm.reset();
    this.sortieForm.reset();
    this.congeForm.reset();
    this.motifForm.reset();
    this.files = [];
    this.selectedFormType = null;
    this.oldFormType = null;
  }



  returnUserImg(id: number): string {
    const user = this.currentUser.team.members.find(u => u.id === id)
    return user.imgPath;
  }
  returnUserNom(id: number): string {
    const user = this.currentUser.team.members.find(u => u.id === id)
    return user.nomComplet;
  }


  returnType(type: string, cat: any): string {
    if (type === 'CONGE') {
      return `Congé`;
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

  onFileChange(event: Event): void {
    const element = event.target as HTMLInputElement;
    const fileArray = Array.from(element.files || []);
    this.files = [...this.files, ...fileArray];
  }

  deleteFile(index: number): void {
    this.files.splice(index, 1);
  }

  humanFileSize(size: number): string {
    const i = Math.floor(Math.log(size) / Math.log(1024));
    return `${(size / Math.pow(1024, i)).toFixed(2)} ${['B', 'KB', 'MB', 'GB', 'TB'][i]}`;
  }
}

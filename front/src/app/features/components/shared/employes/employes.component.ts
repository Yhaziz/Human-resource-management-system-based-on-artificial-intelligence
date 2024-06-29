import { BrnDialogContentDirective, BrnDialogTriggerDirective } from '@spartan-ng/ui-dialog-brain';
import { HlmDialogComponent, HlmDialogContentComponent, HlmDialogDescriptionDirective, HlmDialogFooterComponent, HlmDialogHeaderComponent, HlmDialogTitleDirective } from '@spartan-ng/ui-dialog-helm';
import { HlmInputDirective } from '@spartan-ng/ui-input-helm';
import { HlmLabelDirective } from '@spartan-ng/ui-label-helm';
import { lucideKey, lucideMonitorCheck, lucideMonitorOff, lucideBell, lucideCross, lucideChevronDown, lucideArrowUpDown, lucideMoreHorizontal } from '@ng-icons/lucide';
import { HlmButtonModule } from '@spartan-ng/ui-button-helm';
import { HlmIconComponent, provideIcons } from '@spartan-ng/ui-icon-helm';
import { BrnMenuTriggerDirective } from '@spartan-ng/ui-menu-brain';
import { HlmMenuModule } from '@spartan-ng/ui-menu-helm';
import { HlmButtonDirective } from '@spartan-ng/ui-button-helm';
import { BrnAlertDialogContentDirective, BrnAlertDialogTriggerDirective } from '@spartan-ng/ui-alertdialog-brain';
import { HlmAlertDialogActionButtonDirective, HlmAlertDialogCancelButtonDirective, HlmAlertDialogComponent, HlmAlertDialogContentComponent, HlmAlertDialogDescriptionDirective, HlmAlertDialogFooterComponent, HlmAlertDialogHeaderComponent, HlmAlertDialogOverlayDirective, HlmAlertDialogTitleDirective } from '@spartan-ng/ui-alertdialog-helm';
import { BrnSelectImports } from '@spartan-ng/ui-select-brain';
import { HlmSelectImports } from '@spartan-ng/ui-select-helm';
import { HlmSwitchComponent } from '@spartan-ng/ui-switch-helm';
import { NgxSonnerToaster, toast } from 'ngx-sonner';

import { DgService } from './../../../../core/services/dg.service';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { User, DepartmentsUsers, Solde } from '../../../../core/types';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-employes',
  standalone: true,
  imports: [
    BrnAlertDialogContentDirective,
    BrnAlertDialogTriggerDirective,
    BrnDialogContentDirective,
    BrnDialogTriggerDirective,
    BrnMenuTriggerDirective,
    BrnSelectImports,

    HlmSelectImports,
    HlmAlertDialogActionButtonDirective,
    HlmAlertDialogCancelButtonDirective,
    HlmAlertDialogComponent,
    HlmAlertDialogContentComponent,
    HlmAlertDialogDescriptionDirective,
    HlmAlertDialogFooterComponent,
    HlmAlertDialogHeaderComponent,
    HlmAlertDialogOverlayDirective,
    HlmAlertDialogTitleDirective,
    HlmButtonModule,
    HlmDialogComponent,
    HlmDialogContentComponent,
    HlmDialogDescriptionDirective,
    HlmDialogFooterComponent,
    HlmDialogHeaderComponent,
    HlmDialogTitleDirective,
    HlmIconComponent,
    HlmInputDirective,
    HlmLabelDirective,
    HlmMenuModule,
    HlmButtonDirective,
    HlmSwitchComponent,
    ReactiveFormsModule,
    CommonModule,
    NgxSonnerToaster
  ],
  providers: [provideIcons({ lucideKey, lucideMonitorCheck, lucideMonitorOff, lucideBell, lucideChevronDown, lucideArrowUpDown, lucideCross,lucideMoreHorizontal })],
  templateUrl: './employes.component.html',
  styleUrl: './employes.component.css'
})
export class EmployesComponent {
  addForm: FormGroup;
  soldeForm: FormGroup;
  updateForm: FormGroup;
  departements: DepartmentsUsers[];
  users: User[];
  soldes: Solde[];
  selectedUserId: number;
  userEnabled: boolean;
  userIndependant: boolean;
  dialog: any;
  protected readonly toast = toast;

  constructor(private fb: FormBuilder, private dgService: DgService) {
    this.soldeForm = this.fb.group({
      id: ['', Validators.required],
      userId: ['', Validators.required],
      totalCongeJours: ['', Validators.required],
      prisCongeJours: ['', Validators.required],
      totalSortieMin: ['', Validators.required],
      prisSortieMin: ['', Validators.required],
      compteurSortie: ['', Validators.required],
    });
    this.addForm = this.fb.group({
      cin: ['', Validators.required],
      nomComplet: ['', Validators.required],
      qualification: ['', Validators.required],
      sexe: ['', Validators.required],
      telephone: ['', Validators.required],
      dob: ['', Validators.required],
      doj: ['', Validators.required],
      departementId: ['', Validators.required],
    });

    this.updateForm = this.fb.group({
      id: ['', Validators.required],
      cin: ['', Validators.required],
      nomComplet: ['', Validators.required],
      qualification: ['', Validators.required],
      sexe: ['', Validators.required],
      telephone: ['', Validators.required],
      dob: ['', Validators.required],
      doj: ['', Validators.required],
      departementId: ['', Validators.required],
      independant: ['', Validators.required],
      enabled: ['', Validators.required],

    });
  }

  ngOnInit() {
    this.refreshData();
  }

  async refreshData() {
    await this.dgService.fetchData();
    await this.dgService.fetchSolde();
    this.departements = this.dgService.dataDepartmentsUsers();
    this.users = this.dgService.dataUsers();
    this.soldes = this.dgService.soldes();
  }

  selectedUser(id: number, type: number) {
    this.selectedUserId = id;


    setTimeout(()=>{
      if (type === 2) {
        const user = this.users.find(u => u.id === id);
      if(user.enabled){
        this.userEnabled = true;
        console.log("this.userEnabled = true;")
      }else{
        this.userEnabled = false;
        console.log("this.userEnabled = false;")
      }

      if(user.independant){
        this.userIndependant = true;
      }else{
        this.userIndependant = false;
      }
      this.updateForm.patchValue({
        id: user.id,
        cin: user.cin,
        nomComplet: user.nomComplet,
        qualification: user.qualification,
        sexe: user.sexe,
        telephone: user.telephone,
        dob: user.dob,
        doj: user.doj,
        departementId: "" + user.departementId,
        independant: user.independant,
        enabled: user.enabled,
      });
      }

      if (type === 1) {
        const solde = this.soldes.find(s => s.userId === id)
      this.soldeForm.patchValue({
        id: solde.id,
        userId: solde.userId,
        totalCongeJours: solde.totalCongeJours,
        totalSortieMin: solde.totalSortieMin,
        prisCongeJours: solde.prisCongeJours,
        prisSortieMin: solde.prisSortieMin,
        compteurSortie: solde.compteurSortie
      });
      }

    }, 0.00000000000000001)

  }

  async updateSolde() {
    if (this.soldeForm.valid) {
      await this.dgService.updateSolde(this.soldeForm.value.userId, {
        id: Number(this.soldeForm.value.id),
        userId: Number(this.soldeForm.value.userId),
        totalCongeJours: Number(this.soldeForm.value.totalCongeJours),
        totalSortieMin: Number(this.soldeForm.value.totalSortieMin),
        prisCongeJours: Number(this.soldeForm.value.prisCongeJours),
        prisSortieMin: Number(this.soldeForm.value.prisSortieMin),
        compteurSortie: Number(this.soldeForm.value.compteurSortie)
      });
      this.toast('Solde Modifié avec succés.');
      this.refreshData();
      this.dialog.close();
    }else{
      toast.error("Échec, veuillez vérifier les données");
    }
  }

  async addUser() {
    if (this.addForm.valid) {
      await this.dgService.addUser({
        cin: this.addForm.value.cin,
        nomComplet: this.addForm.value.nomComplet,
        qualification: this.addForm.value.qualification,
        sexe: this.addForm.value.sexe,
        telephone: this.addForm.value.telephone,
        dob: this.addForm.value.dob,
        doj: this.addForm.value.doj,
        departementId: this.addForm.value.departementId,

      });
      this.toast('Utilisateur Ajouté avec succés.');
      this.refreshData();
      this.addForm.reset();
      this.dialog.close();
    }else{
      toast.error("Échec, veuillez vérifier les données");
    }
  }

  async updateUser() {
    if (this.updateForm.valid) {
      await this.dgService.updateUser(this.updateForm.value.id, {
        cin: this.updateForm.value.cin,
        nomComplet: this.updateForm.value.nomComplet,
        qualification: this.updateForm.value.qualification,
        sexe: this.updateForm.value.sexe,
        telephone: this.updateForm.value.telephone,
        dob: this.updateForm.value.dob,
        doj: this.updateForm.value.doj,
        departementId: this.updateForm.value.departementId,
        independant: this.updateForm.value.independant,
        enabled: this.updateForm.value.enabled,
      });
      this.toast('Utilisateur Modifié avec succés.');
      this.refreshData();
      this.dialog.close();
    }else{
      toast.error("Échec, veuillez vérifier les données");
    }
  }

  async resetPass() {
    await this.dgService.resetPass(this.selectedUserId);
    this.toast('Mot de Passe Réintialisé avec succés.');
    this.refreshData();
    this.dialog.close();
  }

  async deleteUser() {
    if (this.selectedUserId) {
      await this.dgService.deleteUser(this.selectedUserId);
      this.toast('Utilisateur Supprimé avec succés.');
      this.refreshData();
      this.dialog.close();
    }
  }
  passDialog(ctx: any) {
    this.dialog = ctx;
  }


}

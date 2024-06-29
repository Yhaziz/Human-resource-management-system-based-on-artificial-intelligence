import { BrnDialogContentDirective, BrnDialogTriggerDirective } from '@spartan-ng/ui-dialog-brain';
import { HlmDialogComponent, HlmDialogContentComponent, HlmDialogDescriptionDirective, HlmDialogFooterComponent, HlmDialogHeaderComponent, HlmDialogTitleDirective } from '@spartan-ng/ui-dialog-helm';
import { HlmInputDirective } from '@spartan-ng/ui-input-helm';
import { HlmLabelDirective } from '@spartan-ng/ui-label-helm';
import { lucideAlertCircle, lucideCross, lucideArrowUpDown, lucideChevronDown, lucideMoreHorizontal } from '@ng-icons/lucide';
import { HlmButtonModule } from '@spartan-ng/ui-button-helm';
import { HlmIconComponent, provideIcons } from '@spartan-ng/ui-icon-helm';
import { BrnMenuTriggerDirective } from '@spartan-ng/ui-menu-brain';
import { HlmMenuModule } from '@spartan-ng/ui-menu-helm';
import { HlmButtonDirective } from '@spartan-ng/ui-button-helm';
import { BrnAlertDialogContentDirective, BrnAlertDialogTriggerDirective } from '@spartan-ng/ui-alertdialog-brain';
import { HlmAlertDialogActionButtonDirective, HlmAlertDialogCancelButtonDirective, HlmAlertDialogComponent, HlmAlertDialogContentComponent, HlmAlertDialogDescriptionDirective, HlmAlertDialogFooterComponent, HlmAlertDialogHeaderComponent, HlmAlertDialogOverlayDirective, HlmAlertDialogTitleDirective } from '@spartan-ng/ui-alertdialog-helm';
import { HlmAvatarImageDirective, HlmAvatarComponent, HlmAvatarFallbackDirective } from '@spartan-ng/ui-avatar-helm';
import { BrnSelectImports } from '@spartan-ng/ui-select-brain';
import { HlmSelectImports } from '@spartan-ng/ui-select-helm';

import { CommonModule } from '@angular/common';
import { DgService } from './../../../../core/services/dg.service';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { DepartmentsUsers } from '../../../../core/types';
import { NgxSonnerToaster, toast } from 'ngx-sonner';



@Component({
  selector: 'app-departements',
  standalone: true,
  imports: [
    BrnAlertDialogContentDirective,
    BrnAlertDialogTriggerDirective,
    BrnDialogContentDirective,
    BrnDialogTriggerDirective,
    BrnMenuTriggerDirective,
    BrnSelectImports,


    HlmSelectImports,
    HlmAvatarImageDirective, HlmAvatarComponent, HlmAvatarFallbackDirective,
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
    ReactiveFormsModule,
    CommonModule,
    NgxSonnerToaster,

  ],
  providers: [provideIcons({ lucideAlertCircle, lucideCross, lucideArrowUpDown, lucideChevronDown, lucideMoreHorizontal })],
  host: {
    class: 'w-full',
  },
  templateUrl: './departements.component.html',
  styleUrl: './departements.component.css'
})


export class DepartementsComponent {

  addForm: FormGroup;
  updateForm: FormGroup;
  departements: DepartmentsUsers[];
  selectedDeptId: number;
  selectedDept: DepartmentsUsers;
  dialog: any;
  protected readonly toast = toast;

  constructor(private fb: FormBuilder, private dgService: DgService) {
    this.addForm = this.fb.group({
      name: ['', [Validators.required, this.validateCharacters]]
    });

    this.updateForm = this.fb.group({
      id: ['', Validators.required],
      name: ['', Validators.required],
      type: ['', Validators.required],
      chefId: ['']
    });
  }

  ngOnInit() {
    this.refreshData();
  }

  async refreshData() {
    await this.dgService.fetchData();
    this.departements = this.dgService.dataDepartmentsUsers();
  }

  selectedDepartment(id: number) {
    this.selectedDeptId = id;

    const department = this.departements.find(d => d.id === id);
    this.selectedDept = department;
    setTimeout(() => {
      this.updateForm.patchValue({
        id: department.id,
        name: department.name,
        type: department.type,
        chefId: "" + department.chef?.id || ''

      });
    });
  }

  async addDepartment() {
    if (this.addForm.valid) {
      await this.dgService.addDepartment({
        name: this.addForm.value.name,
        type: 'AUTRE',
        chefId: null

      });

      this.refreshData();
      this.addForm.reset();
      this.dialog.close();

    }
  }

  async updateDepartment() {
    if (this.updateForm.valid) {
      await this.dgService.updateDepartment(this.updateForm.value.id, {
        name: this.updateForm.value.name,
        chefId: Number(this.updateForm.value.chefId) || null,
        type: this.updateForm.value.type
      });
      this.toast('Départements modifier avec succès');
      this.refreshData();
      this.dialog.close();
    }
  }

  async deleteDepartment() {
    if (this.selectedDeptId) {
      await this.dgService.deleteDepartment(this.selectedDeptId);
      this.toast('Départements supprimer avec succès');
      this.refreshData();
      this.dialog.close();
    }
  }
  passDialog(ctx: any) {
    this.dialog = ctx;
  }

  validateCharacters(control) {
    const value = control.value;
    if (/^[A-Za-z\s]+$/.test(value)) {
      return null;
    } else {
      return { invalidCharacters: true };
    }
  }
}


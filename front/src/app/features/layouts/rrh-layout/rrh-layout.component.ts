import { Component, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';

import { HlmScrollAreaComponent } from '@spartan-ng/ui-scrollarea-helm';
import { BrnSeparatorComponent } from '@spartan-ng/ui-separator-brain';
import { HlmSeparatorDirective } from '@spartan-ng/ui-separator-helm';

import { BrnMenuTriggerDirective } from '@spartan-ng/ui-menu-brain';
import { HlmMenuComponent, HlmMenuGroupComponent, HlmMenuItemDirective, HlmMenuItemIconDirective, HlmMenuItemSubIndicatorComponent, HlmMenuLabelComponent, HlmMenuSeparatorComponent, HlmMenuShortcutComponent, HlmSubMenuComponent } from '@spartan-ng/ui-menu-helm';

import { lucideTrash2, lucideLogOut, lucideCog } from '@ng-icons/lucide';
import { HlmIconComponent, provideIcons } from '@spartan-ng/ui-icon-helm';

import { HlmAvatarImageDirective, HlmAvatarComponent, HlmAvatarFallbackDirective } from '@spartan-ng/ui-avatar-helm';


import { lucideCross } from '@ng-icons/lucide';



import { HlmButtonDirective } from '@spartan-ng/ui-button-helm';
import { HlmInputDirective } from '@spartan-ng/ui-input-helm';
import { HlmLabelDirective } from '@spartan-ng/ui-label-helm';
import { BrnSheetContentDirective, BrnSheetTriggerDirective } from '@spartan-ng/ui-sheet-brain';
import { HlmSheetComponent, HlmSheetContentComponent, HlmSheetDescriptionDirective, HlmSheetFooterComponent, HlmSheetHeaderComponent, HlmSheetTitleDirective } from '@spartan-ng/ui-sheet-helm';


import { HlmCardContentDirective, HlmCardDescriptionDirective, HlmCardDirective, HlmCardFooterDirective, HlmCardHeaderDirective, HlmCardTitleDirective } from '@spartan-ng/ui-card-helm';
import { HlmTabsComponent, HlmTabsContentDirective, HlmTabsListComponent, HlmTabsTriggerDirective } from '@spartan-ng/ui-tabs-helm';
import { AuthService } from '../../../core/services/auth.service';
import { DgService } from '../../../core/services/dg.service';
import { CurrentUserData } from '../../../core/types';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';



@Component({
  selector: 'app-dg-layout',
  standalone: true,

  imports: [
    RouterModule,
    HlmScrollAreaComponent,
    HlmSeparatorDirective,
    BrnSeparatorComponent,
    BrnMenuTriggerDirective,
    HlmMenuComponent,
    HlmMenuGroupComponent,
    HlmMenuItemDirective,
    HlmMenuItemIconDirective,
    HlmMenuItemSubIndicatorComponent,
    HlmMenuLabelComponent,
    HlmMenuSeparatorComponent,
    HlmMenuShortcutComponent,
    HlmSubMenuComponent,
    HlmIconComponent,

    HlmAvatarImageDirective, HlmAvatarComponent, HlmAvatarFallbackDirective,


    BrnSheetTriggerDirective,
    BrnSheetContentDirective,
    HlmSheetComponent,
    HlmSheetContentComponent,
    HlmSheetHeaderComponent,
    HlmSheetFooterComponent,
    HlmSheetTitleDirective,
    HlmSheetDescriptionDirective,
    HlmButtonDirective,
    HlmInputDirective,
    HlmIconComponent,
    HlmLabelDirective,

    HlmTabsComponent,
    HlmTabsListComponent,
    HlmTabsTriggerDirective,
    HlmTabsContentDirective,

    HlmCardContentDirective,
    HlmCardDescriptionDirective,
    HlmCardDirective,
    HlmCardFooterDirective,
    HlmCardHeaderDirective,
    HlmCardTitleDirective,

    HlmLabelDirective,
    HlmInputDirective,
    HlmButtonDirective,
    ReactiveFormsModule,




  ],

  providers: [
    provideIcons({
      lucideLogOut, lucideCog, lucideCross, lucideTrash2
    }),
  ],
  templateUrl: './rrh-layout.component.html',
  styleUrl: './rrh-layout.component.css'
})
export class RrhLayoutComponent {
  authService = inject(AuthService);
  dgService = inject(DgService);
  router = inject(Router);


  profileForm: FormGroup;
  passChangeForm: FormGroup;
  img: File | null = null;
  currentUserData: CurrentUserData = null;


  constructor(private fb: FormBuilder){
    this.profileForm = this.fb.group({
      nomComplet: ['', Validators.required],
      telephone: ['', Validators.required],
    });

    this.passChangeForm = this.fb.group({
      oldPass: ['', Validators.required],
      newPass: ['', Validators.required],
      newPassConfirmation: ['', Validators.required],
    });
  }

  ngOnInit() {
    if (!(localStorage.getItem('jwt') && localStorage.getItem('role'))) {
      this.authService.cleanData();
      this.router.navigateByUrl('/login');
    }

    this.dashboardBootstrap()

  }
  async dashboardBootstrap() {
    await this.dgService.dashboardBootstrap()
    if (this.dgService.currentUserData().user.role !== localStorage.getItem('role')) {
      this.authService.cleanData();
      this.router.navigateByUrl('/login');
    } else {
      console.log("Service: ", this.dgService.currentUserData());
      this.currentUserData = this.dgService.currentUserData();
      console.log("Layout: ", this.currentUserData);
      this.profileForm.setValue({
        nomComplet: this.currentUserData.user.nomComplet,
        telephone: this.currentUserData.user.telephone,

      });
    }
  }


  openNav(): void {
    const sidebar = document.querySelector<HTMLElement>("aside");
    const maxSidebar = document.querySelector<HTMLElement>(".max");
    const miniSidebar = document.querySelector<HTMLElement>(".mini");
    const maxToolbar = document.querySelector<HTMLElement>(".max-toolbar");
    const logo = document.querySelector<HTMLElement>(".logo");
    const content = document.querySelector<HTMLElement>(".content");


    if (sidebar?.classList.contains("-translate-x-48")) {
      // max sidebar
      sidebar?.classList.remove("-translate-x-48");
      sidebar?.classList.add("translate-x-none");
      maxSidebar?.classList.remove("hidden");
      maxSidebar?.classList.add("flex");
      miniSidebar?.classList.remove("flex");
      miniSidebar?.classList.add("hidden");
      maxToolbar?.classList.add("translate-x-0");
      maxToolbar?.classList.remove("translate-x-24", "scale-x-0");
      logo?.classList.remove("ml-12");
      content?.classList.remove("ml-12");
      content?.classList.add("ml-12", "md:ml-60");
    } else {
      // mini sidebar
      sidebar?.classList.add("-translate-x-48");
      sidebar?.classList.remove("translate-x-none");
      maxSidebar?.classList.add("hidden");
      maxSidebar?.classList.remove("flex");
      miniSidebar?.classList.add("flex");
      miniSidebar?.classList.remove("hidden");
      maxToolbar?.classList.add("translate-x-24", "scale-x-0");
      maxToolbar?.classList.remove("translate-x-0");
      logo?.classList.add("ml-12");
      content?.classList.remove("ml-12", "md:ml-60");
      content?.classList.add("ml-12");
    }
  }


  logOut(): void {
    this.authService.logOut().subscribe(res => {
      this.authService.cleanData();
      this.router.navigateByUrl('/login');
    },

    )
  }


  updateProfile(ctx: any){
    const formData = new FormData();
    const data = {
      id: this.currentUserData.user.id,
      nomComplet: this.profileForm.value.nomComplet,
      telephone: this.profileForm.value.telephone,
    }
    formData.append("data", JSON.stringify(data));
    if (this.img) {
      formData.append(`file`, this.img);
    }
    if(this.dgService.updateProfile(formData)){
      setTimeout(()=>{
        this.ngOnInit();
        ctx.close();
        this.img = null;
      }, 100);
    }
  }

  changePassword(ctx){
    const data = {
      currentPassword: this.passChangeForm.value.oldPass,
      newPassword: this.passChangeForm.value.newPass,
      confirmPassword: this.passChangeForm.value.newPassConfirmation,
    }
    if(this.dgService.updatePassword(data)){
      setTimeout(()=>{
        this.ngOnInit();
        ctx.close();
        this.img = null;
      }, 100);
    }
  }


  onFileChange(event: Event): void {
    const element = event.target as HTMLInputElement;
    const file = element.files?.[0] || null;
    this.img = file;
  }

  deleteImg(): void {
    this.img = null;
  }

  humanFileSize(size: number): string {
    const i = Math.floor(Math.log(size) / Math.log(1024));
    return `${(size / Math.pow(1024, i)).toFixed(2)} ${['B', 'KB', 'MB', 'GB', 'TB'][i]}`;
  }


}

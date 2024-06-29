import { Component, inject } from '@angular/core';
import { HlmCardContentDirective, HlmCardDescriptionDirective, HlmCardDirective, HlmCardFooterDirective, HlmCardHeaderDirective, HlmCardTitleDirective, } from '@spartan-ng/ui-card-helm';
import { HlmInputDirective } from '@spartan-ng/ui-input-helm';
import { HlmLabelDirective } from '@spartan-ng/ui-label-helm';
import { DgService } from '../../../../core/services/dg.service';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { PlatformConfig } from '../../../../core/types';

import { NgxSonnerToaster, toast } from 'ngx-sonner';
import { HlmIconComponent, provideIcons } from '@spartan-ng/ui-icon-helm';
import { lucideCheckCheck, lucideX } from '@ng-icons/lucide';
import { HlmSwitchComponent } from '@spartan-ng/ui-switch-helm';
import { HlmButtonDirective } from '@spartan-ng/ui-button-helm';

@Component({
  selector: 'app-parametres',
  standalone: true,
  imports: [
    HlmCardContentDirective,
    HlmCardDescriptionDirective,
    HlmCardDirective,
    HlmCardFooterDirective,
    HlmCardHeaderDirective,
    HlmCardTitleDirective,
    HlmInputDirective,
    HlmLabelDirective,
    NgxSonnerToaster,
    HlmIconComponent,
    HlmSwitchComponent,
    ReactiveFormsModule,
    HlmButtonDirective
  ],
  providers: [provideIcons({ lucideCheckCheck, lucideX })],
  templateUrl: './parametres.component.html',
  styleUrl: './parametres.component.css'
})
export class ParametresComponent {
  configForm: FormGroup;
  config: PlatformConfig;
  ai: boolean = false;
  sms: boolean = false;
  protected readonly toast = toast;

  constructor(private fb: FormBuilder, private dgService: DgService) {
    this.configForm = this.fb.group({
      aiFeature: ['', Validators.required],
      apiKey: ['', Validators.required],
      model: ['', Validators.required],
      temperature: ['', Validators.required],
      smsFeature: ['', Validators.required],
      smsUsername: ['', Validators.required],
      smsPassword: ['', Validators.required],
    });
    this.config = dgService.config();
  }
  ngOnInit() {
    setTimeout(()=>{
      this.configForm.patchValue({
        aiFeature: this.config.aiFeature,
        apiKey: this.config.apiKey,
        model: this.config.model,
        temperature: this.config.temperature,
        smsFeature: this.config.smsFeature,
        smsUsername: this.config.smsUsername,
        smsPassword: this.config.smsPassword,
      });
      this.ai = this.config.aiFeature;
      this.sms = this.config.smsFeature;
    }, 1)
  }


  async updateConfig(){
    if(this.configForm.valid){
      await this.dgService.updateConfig({
        aiFeature: Boolean(this.configForm.value.aiFeature),
        apiKey: this.configForm.value.apiKey,
        model: this.configForm.value.model,
        temperature: Number(this.configForm.value.temperature),
        smsFeature: Boolean(this.configForm.value.smsFeature),
        smsUsername: this.configForm.value.smsUsername,
        smsPassword: this.configForm.value.smsPassword,
      });
      toast.success("Paramètres Modifiés avec succés.");
    }else{
      toast.error("Échec, veuillez vérifier les données");
    }
  }


}

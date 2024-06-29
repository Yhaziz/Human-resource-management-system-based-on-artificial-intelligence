import { Notif } from './../../../../core/types/index';
import { DgService } from './../../../../core/services/dg.service';
import { Component, inject } from '@angular/core';

import { HlmAvatarImageDirective, HlmAvatarComponent, HlmAvatarFallbackDirective } from '@spartan-ng/ui-avatar-helm';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [HlmAvatarImageDirective, HlmAvatarComponent, HlmAvatarFallbackDirective],
  templateUrl: './notification.component.html',
  styleUrl: './notification.component.css'
})
export class NotificationComponent {
  dgService = inject(DgService);
  seenNotifs: Notif[] = [];
  notSeenNotifs: Notif[] = [];

  ngOnInit(){
    this.seenNotifs = this.dgService.currentUserData().notifications.seen;
    this.notSeenNotifs = this.dgService.currentUserData().notifications.notSeen;

    this.dgService.seeAllNotifications();
  }



}

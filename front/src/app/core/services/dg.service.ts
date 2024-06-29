import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable } from 'rxjs';
import { toast } from 'ngx-sonner';
import { Department, User, Demande, Solde, Notif, DepartmentsUsers, CurrentUserData, DemandesData, StatusDemande, PlatformConfig } from '../types';

const BASE_URL = ['http://localhost:8081']

@Injectable({
  providedIn: 'root'
})
export class DgService {

  http = inject(HttpClient);


  dataDepartmentsUsers = signal<DepartmentsUsers[]>([]);
  dataDemandes = signal<DemandesData>(null);
  dataUsers = signal<User[]>([]);
  currentUserData = signal<CurrentUserData>(null);
  config = signal<PlatformConfig>(null);
  soldes = signal<Solde[]>([]);


  //////////////////////////////////////////// All Users Features ////////////////////////////////////////////
  /////////////////// BootStrapping :
  async dashboardBootstrap() {
    try {
      const loggedInUser = await this.http.get<User>(BASE_URL + '/whoAmI').toPromise();

      // Make sure loggedInUser is fetched successfully before proceeding
      const [teamMembersResponse, teamDemandesResponse, demandesResponse, soldeResponse, notificationsResponse, aiFeature] = await Promise.all([
        this.http.get<User[]>(`${BASE_URL}/${loggedInUser.role}/team`).toPromise(),
        this.http.get<Demande[]>(`${BASE_URL}/${loggedInUser.role}/team/demandes`).toPromise(),
        this.http.get<Demande[]>(`${BASE_URL}/${loggedInUser.role}/mes-demandes`).toPromise(),
        this.http.get<Solde>(`${BASE_URL}/${loggedInUser.role}/solde`).toPromise(),
        this.http.get<Notif[]>(`${BASE_URL}/${loggedInUser.role}/notifications`).toPromise(),
        this.http.get<boolean>(`${BASE_URL}/${loggedInUser.role}/isAIenabled`).toPromise(),

      ]);

      const mergedData = this.mergeBootstrapData(loggedInUser, teamMembersResponse, teamDemandesResponse, demandesResponse, soldeResponse, notificationsResponse, aiFeature);
      this.currentUserData.set(mergedData);
      if (this.currentUserData().user.role == 'DG' || this.currentUserData().user.role == 'RRH' || this.currentUserData().user.role == 'CD'){
        if(this.currentUserData().user.role == 'DG'){
          const configResponse = await this.http.get<PlatformConfig>(`${BASE_URL}/DG/config`).toPromise();
          this.config.set(configResponse);
          console.log("Config :", this.config());
          await this.fetchData();
          await this.fetchDemandes();
          await this.fetchSolde();
        }else if(this.currentUserData().user.role == 'RRH'){
          await this.fetchData();
          await this.fetchDemandes();
          await this.fetchSolde();
        }else{
          await this.fetchDemandes();
        }

      }
    } catch (error) {
      console.error('Failed to BootStrap:', error);
    }
  }

  private mergeBootstrapData(loggedInUser: User, teamMembersResponse: User[], teamDemandesResponse: Demande[], demandes: Demande[], solde: Solde, notifications: Notif[], aiFeature): CurrentUserData {
    // Check if team members exist before sorting
    const sortedTeam = teamMembersResponse.sort((a, b) => ["DG", "RRH", "CD"].indexOf(a.role) - ["DG", "RRH", "CD"].indexOf(b.role));

    // Merge demandes from team endpoint and individual endpoint
    const mergedTeamDemandes = [...teamDemandesResponse];

    // Filter and sort demandes
    const pendingDemandes = demandes.filter(d => !d.canceled && d.statusDg !== 'REJECTED' && d.statusRrh !== 'REJECTED' && d.statusCd !== 'REJECTED' && (d.statusDg !== 'ACCEPTED' || d.statusRrh !== 'ACCEPTED' || d.statusCd !== 'ACCEPTED'));
    const decidedDemandes = demandes.filter(d => d.canceled || d.statusDg === 'REJECTED' || d.statusRrh === 'REJECTED' || d.statusCd === 'REJECTED' || (d.statusDg === 'ACCEPTED' && d.statusRrh === 'ACCEPTED' && d.statusCd === 'ACCEPTED'));
    pendingDemandes.sort((a, b) => {
      const dateA = new Date(a.dateCreation);
      const dateB = new Date(b.dateCreation);
      return dateB.getTime() - dateA.getTime();
    });
    decidedDemandes.sort((a, b) => {
      const dateA = new Date(a.dateCreation);
      const dateB = new Date(b.dateCreation);
      return dateB.getTime() - dateA.getTime();
    });

    // Filter and sort notifications
    const seenNotifications = notifications.filter(n => n.vue);
    const notSeenNotifications = notifications.filter(n => !n.vue);
    seenNotifications.sort((a, b) => {
      const dateA = new Date(a.dateCreation);
      const dateB = new Date(b.dateCreation);
      return dateB.getTime() - dateA.getTime();
    });
    notSeenNotifications.sort((a, b) => {
      const dateA = new Date(a.dateCreation);
      const dateB = new Date(b.dateCreation);
      return dateB.getTime() - dateA.getTime();
    });

    return {
      user: loggedInUser,
      solde,
      team: { members: sortedTeam, demandes: mergedTeamDemandes },
      demandes: { pending: pendingDemandes, decided: decidedDemandes },
      notifications: { seen: seenNotifications, notSeen: notSeenNotifications },
      ai: aiFeature
    };
  }
  //////////////////// Settings :
  async updateProfile(data: any) {
    try {
      await this.http.put(`${BASE_URL}/${this.currentUserData().user.role}/settings/updateProfile`, data).toPromise();
      return true;
    } catch (error) {
      console.error('Failed to update Profile: ', error);
      toast.error(error);
      return false;
    }
  }

  async updatePassword(data: any) {
    try {
      console.log(data);
      await this.http.put(`${BASE_URL}/${this.currentUserData().user.role}/settings/updatePassword`, data).toPromise();
      return true;
    } catch (error) {
      console.error('Failed to update Password: ', error);
      toast.error(error);
      return false;
    }
  }
  //////////////////// Demandes :
  async makeDemande(formData: any, files: any) {
    try {
      await this.http.post(`${BASE_URL}/${this.currentUserData().user.role}/mes-demandes/add`, formData, files).toPromise();
      return true;
    } catch (error) {
      console.error('Failed to Make Demande: ', error);
      toast.error(error);
      return false;
    }
  }
  async cancelDemande(id: number){
    try {
      await this.http.patch(`${BASE_URL}/${this.currentUserData().user.role}/mes-demandes/${id}/cancel`, null).toPromise();
      return true;
    } catch (error) {
      console.error('Failed to Cancel Demande: ', error);
      toast.error(error);
      return false;
    }
  }
  //////////////////// Notifications :
  async seeAllNotifications() {
    try {
      if(this.currentUserData().notifications.notSeen.length > 0) {
        await this.http.patch(`${BASE_URL}/${this.currentUserData().user.role}/notifications/seeAll`, null).toPromise();
        await this.dashboardBootstrap();

      }
      return true;
      }catch (error) {
      console.error('Failed to Mark as seen: ', error);
      toast.error(error);
      return false;
    }
  }
  //////////////////////////////////////////// DG & RRH Features ////////////////////////////////////////////

  /////////////// Fetch and Merge Departments & Users
  async fetchData() {
    try {
      if( this.currentUserData().user.role =='DG' || this.currentUserData().user.role =='RRH'){
        const [departments, users] = await Promise.all([
          this.http.get<Department[]>(`${BASE_URL}/${this.currentUserData().user.role}/departements`).toPromise(),
          this.http.get<User[]>(`${BASE_URL}/${this.currentUserData().user.role}/users`).toPromise(),
        ]);
        this.mergeData(departments, users);
        this.dataUsers.set(users);
      }
    } catch (error) {
      console.error('Failed to fetch Departments & Users data:', error);
      toast.error(error);
    }
  }

  private mergeData(departments: Department[], users: User[]) {
    const mergedData = departments.map(department => {
      const deptUsers = users.filter(user => user.departementId === department.id);
      const chef = users.find(user => user.id === department.chefId);
      return {
        id: department.id,
        name: department.name,
        type: department.type,
        chef: chef || null,
        users: deptUsers
      };
    });
    this.dataDepartmentsUsers.set(mergedData);
  }
  /////////////// Fetch and organize Demandes
  async fetchDemandes() {
    try {
      if( this.currentUserData().user.role =='DG' || this.currentUserData().user.role =='RRH' || this.currentUserData().user.role =='CD'){
        const [decidedResponse, toDecideResponse] = await Promise.all([
          this.http.get<Demande[]>(`${BASE_URL}/${this.currentUserData().user.role}/demandes/decided-by-all`).toPromise(),
          this.http.get<Demande[]>(`${BASE_URL}/${this.currentUserData().user.role}/demandes/to-decide-by-me`).toPromise()
        ]);
        console.log("decidedResponse : ", decidedResponse);
        console.log("toDecideResponse : ", toDecideResponse);
        this.organizeDemandes(decidedResponse, toDecideResponse);

      }
    } catch (error) {
      console.error('Failed to fetch Demandes data:', error);
      toast.error(error);
    }
  }

  private organizeDemandes(decided: Demande[], toDecide: Demande[]) {
    const sortedDemandes: DemandesData = {
      decided: {
        all: decided.sort((a, b) => new Date(b.dateReponse).getTime() - new Date(a.dateReponse).getTime()),
        accepted: [],
        rejected: [],
        canceled: []
      },
      toDecided: toDecide.sort((a, b) => new Date(b.dateCreation).getTime() - new Date(a.dateCreation).getTime())
    };

    decided.forEach(demande => {
      if (!demande.canceled && demande.statusDg === StatusDemande.ACCEPTED && demande.statusRrh === StatusDemande.ACCEPTED && demande.statusCd === StatusDemande.ACCEPTED) {
        sortedDemandes.decided.accepted.push(demande);
      } else if (!demande.canceled && demande.statusDg === StatusDemande.REJECTED || demande.statusRrh === StatusDemande.REJECTED || demande.statusCd === StatusDemande.REJECTED) {
        sortedDemandes.decided.rejected.push(demande);
      } else if (demande.canceled) {
        sortedDemandes.decided.canceled.push(demande);
      }
    });

    this.dataDemandes.set(sortedDemandes);
  }
  /////////////// Fetch Solde
  async fetchSolde() {
    try {
      if( this.currentUserData().user.role =='DG' || this.currentUserData().user.role =='RRH'){
        const [soldes] = await Promise.all([
          this.http.get<Solde[]>(`${BASE_URL}/${this.currentUserData().user.role}/solde/all`).toPromise()
        ]);
        this.soldes.set(soldes);
      }
    } catch (error) {
      console.error('Failed to fetch Solde data:', error);
      toast.error(error);
    }
  }
  /////////////// Update Solde
  async updateSolde(userId:number, updateData: any) {
    try {
      await this.http.put(`${BASE_URL}/${this.currentUserData().user.role}/solde/${userId}/update`, updateData).toPromise();
    } catch (error) {
      console.error('Failed to update Solde:', error);
      toast.error(error);
    }
  }


  /////////////// Departements Crud
  async addDepartment(departmentData: any) {
    try {
      await this.http.post(`${BASE_URL}/${this.currentUserData().user.role}/departements/add`, departmentData).toPromise();
    } catch (error) {
      console.error('Failed to add department:', error);
      toast.error(error);
    }
  }

  async updateDepartment(departmentId: number, updateData: any) {
    try {
      await this.http.put(`${BASE_URL}/${this.currentUserData().user.role}/departements/${departmentId}/update`, updateData).toPromise();
    } catch (error) {
      console.error('Failed to update department:', error);
      toast.error(error);
    }
  }

  async deleteDepartment(departmentId: number) {
    try {
      await this.http.delete(`${BASE_URL}/${this.currentUserData().user.role}/departements/${departmentId}/delete`).toPromise();
    } catch (error) {
      console.error('Failed to delete department:', error);
      toast.error(error);
    }
  }
  /////////////// Users Crud
  async addUser(userData: any) {
    try {
      await this.http.post(`${BASE_URL}/${this.currentUserData().user.role}/users/add`, userData).toPromise();
    } catch (error) {
      console.error('Failed to add user:', error);
      toast.error(error);
    }
  }

  async updateUser(userId: number, updateData: any) {
    console.log("Update Value : ", updateData)
    try {
      await this.http.put(`${BASE_URL}/${this.currentUserData().user.role}/users/${userId}/update`, updateData).toPromise();
    } catch (error) {
      console.error('Failed to update user:', error);
      toast.error(error);
    }
  }

  async resetPass(userId: number) {
    try {
      this.http.patch(`${BASE_URL}/${this.currentUserData().user.role}/users/${userId}/resetPassword`, null);
    } catch (error) {
      console.error('Failed to Reset Password:', error);
      toast.error(error);
    }
  }

  async deleteUser(userId: number) {
    try {
      await this.http.delete(`${BASE_URL}/${this.currentUserData().user.role}/users/${userId}/delete`).toPromise();
    } catch (error) {
      console.error('Failed to delete user:', error);
      toast.error(error);
    }
  }

  /////////////// Demande Accept/Reject
  async acceptDemande(id: number) {
    try {
      console.log(id)
      await this.http.patch(`${BASE_URL}/${this.currentUserData().user.role}/demandes/${id}/accept`, null).toPromise();
    } catch (error) {
      console.error('Failed to acceptDemande:', error);
      toast.error(error);
    }
  }
  async rejectDemande(id: number, note: string) {
    try {
      await this.http.patch(`${BASE_URL}/${this.currentUserData().user.role}/demandes/${id}/reject`, note).toPromise();
    } catch (error) {
      console.error('Failed to rejectDemande:', error);
      toast.error(error);
    }
  }

  /////////////// Config
  async updateConfig(configData: any) {
    console.log("Update configData : ", configData)
    try {
      await this.http.put(`${BASE_URL}/${this.currentUserData().user.role}/config`, configData).toPromise();
    } catch (error) {
      console.error('Failed to update config:', error);
      toast.error(error);
    }
  }

}

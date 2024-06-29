export type Department = {
  id: number;
  chefId: number;
  name: string;
  type: "DIRECTION" | "RH" | "AUTRE";
};

export type User = {
  id: number;
  cin: number;
  imgPath: string;
  nomComplet: string;
  qualification: string;
  sexe: "MALE" | "FEMALE";
  telephone: string;
  dob: string; // Date of Birth
  doj: string; // Date of Joining
  departementId: number;
  departementName: string;
  independant: boolean;
  role: "DG" | "RRH" | "CD" | "USER";
  enabled: boolean;
  dateCreation: Date;
};


export type Demande = {
  id: number;
  userId: number;
  type: string;
  categorie: string;
  motif: string;
  debut: Date;
  fin: Date;
  attachments: string[];
  statusDg: StatusDemande;
  statusRrh: StatusDemande;
  statusCd: StatusDemande;
  dateCreation: Date;
  dateReponse: Date;
  noteResponse: string;
  canceled: boolean;
};

export type PlatformConfig = {
  aiFeature: boolean;
  smsFeature: boolean;
  smsUsername: String;
  smsPassword: String;

  apiKey: String;
  model: String;
  temperature: number;
}

export type DemandesData = {
  decided: {
    all: Demande[];
    accepted: Demande[];
    rejected: Demande[];
    canceled: Demande[];
  };
  toDecided: Demande[];
};

export enum StatusDemande {
  REJECTED = 'REJECTED',
  ACCEPTED = 'ACCEPTED',
  PENDING = 'PENDING'
}

export type Solde = {
  id: number;
  userId: number;
  totalCongeJours: number;
  prisCongeJours: number;
  totalSortieMin: number;
  prisSortieMin: number;
  compteurSortie: number;
};

export type Notif = {
  id: number;
  origineNom: string;
  origineImgPath: string;
  text: string;
  vue: boolean;
  dateCreation: Date;
};

export type CurrentUserData = {
  user: User;
  solde: Solde;
  team: {
    members: User[];
    demandes: Demande[];
  };
  demandes: {
    pending: Demande[];
    decided: Demande[];
  };
  notifications: {
    seen: Notif[];
    notSeen: Notif[];
  };
  ai: boolean;
};

export type DepartmentsUsers = {
  id: number;
  name: string;
  type: "DIRECTION" | "RH" | "AUTRE";
  chef: User;
  users: User[];
};

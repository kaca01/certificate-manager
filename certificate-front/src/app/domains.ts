export interface Role {
  id: number,
  name: string,
  authority: string
}

export interface User {
    id: number,
    name: string,
    surname: string,
    phone: string,
    country: string,
    password: string,
    email: string,
    roles: Role[]
}

export interface ResetPassword {  
  newPassword: string,
  firstRepetedPassword: string,
  secondRepetedPassword: string, 
  code: string
}

export interface CertificateRequest {
  _id: number;
  requestType: string,
  issuer: string,
  certificateType: string,
  subject: number,
  refusalReason: number
}

export interface AllRequests {
  totalCount: number;
  results: CertificateRequest[];
}

export interface Certificate {
  _id: number;
  serialNumber: string;
  subject: string;
  validFrom: string;
  validTo: string;
  certificateType: string;
}

export interface AllCertificate {
  totalCount: number;
  results: Certificate[];
}
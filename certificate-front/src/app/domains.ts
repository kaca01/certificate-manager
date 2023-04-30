export interface User {
    id: number,
    name: string,
    surname: string,
    phone: string,
    country: string,
    password: string,
    email: string
}

export interface CertificateRequest {
    requestType: string,
    issuer: string,
    certificateType: string,
    subject: number,
    refusalReason: number
}

export interface Request {
  _id: number;
  issuer: string;
  type: string;
  status: string;
  subject: User;
  refusalReason: string;
}

export interface Certificate {
  _id: number;
  serialNum: string;
  subject: string;
  validFrom: string;
  validTo: string;
  type: string;
}
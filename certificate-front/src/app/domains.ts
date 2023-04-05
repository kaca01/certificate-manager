export interface User {
  _id: number;
  name: string;
  surname: string;
  email: string;
  country: string;
  phone: string;
}

export interface Request {
  _id: number;
  issuer: string;
  type: string;
  status: string;
  subject: User;
  refusalReason: string;
}
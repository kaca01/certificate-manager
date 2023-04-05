import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Request } from 'src/app/domains';

const REQUESTS = [
  {
    _id: 1,
    issuer: "MilicaBojic23",
    type: "INTERMEDIATE",
    status: "ACTIVE",
    subject: {
      _id: 1,
      name: "Jovana",
      surname: "Gordanic",
      email: "joca@gmail.com",
      country: "Siberia",
      phone: "+381672233"
    },
    refusalReason: "",
  },
  {
    _id: 2,
    issuer: "MilicaBojic33",
    type: "ROOT",
    status: "ACTIVE",
    subject: {
      _id: 2,
      name: "Stefanija",
      surname: "Djuric",
      email: "stefanka@gmail.com",
      country: "Mongolia",
      phone: "+381672233"
    },
    refusalReason: "",
  },
  {
    _id: 3,
    issuer: "MilicaBojic44",
    type: "INTERMEDIATE",
    status: "REFUSED",
    subject: {
      _id: 3,
      name: "Milunka",
      surname: "Jaksic",
      email: "mili@gmail.com",
      country: "Nigeria",
      phone: "+381672233"
    },
    refusalReason: "User was spaming",
  },
  {
    _id: 4,
    issuer: "MilicaBojic55",
    type: "END",
    status: "ACCEPTED",
    subject: {
      _id: 4,
      name: "Miki",
      surname: "Mikic",
      email: "miki@gmail.com",
      country: "Russia",
      phone: "+381672233"
    },
    refusalReason: "",
  },
  {
    _id: 5,
    issuer: "MilicaBojic18",
    type: "END",
    status: "REFUSED",
    subject: {
      _id: 5,
      name: "Aco",
      surname: "Acic",
      email: "acke@gmail.com",
      country: "Angola",
      phone: "+381672233"
    },
    refusalReason: "User was spaming",
  }
];


@Injectable({
  providedIn: 'root'
})
export class RequestService {
  private value$ = new BehaviorSubject<any>({});
  selectedValue$ = this.value$.asObservable();

  private requestsList: Request[] = [];

  constructor() { 
    for (let req of REQUESTS) {
      const request: Request = {
        _id: req._id,
        issuer: req.issuer,
        type: req.type,
        status: req.status,
        subject: req.subject,
        refusalReason: req.refusalReason,
      };
      this.requestsList.push(request);
    }
  }
  
    getAllRequests(): Request[] {
      return this.requestsList;
    }
}

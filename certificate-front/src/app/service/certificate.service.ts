import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Certificate, AllCertificate } from '../domains';

const CERTIFICATES = [
  {
    _id: 1,
    serialNumber: '32548732947932',
    subject: 'Marta Maric',
    validFrom: '2021-05-03',
    validTo: '2031-05-03',
    type: 'ROOT',
  },
  {
    _id: 2,
    serialNumber: '345372897489032',
    subject: 'Marko Markovic',
    validFrom: '2022-05-03',
    validTo: '2023-12-12',
    type: 'INTERMEDIATE',
  },
  {
    _id: 3,
    serialNumber: '90573489598317',
    subject: 'Aleksandra Spasic',
    validFrom: '2022-05-03',
    validTo: '2023-12-12',
    type: 'INTERMEDIATE',
  },
  {
    _id: 4,
    serialNumber: '12345678909876543',
    subject: 'Neda Nedimovic',
    validFrom: '2020-12-03',
    validTo: '2022-02-12',
    type: 'INTERMEDIATE',
  },
  {
    _id: 5,
    serialNumber: '12345678909876543',
    subject: 'Nenad Nikolic',
    validFrom: '2022-05-03',
    validTo: '2023-12-12',
    type: 'END',
  },
  {
    _id: 6,
    serialNumber: '12345678909876543',
    subject: 'Petar Nastasic',
    validFrom: '2022-11-10',
    validTo: '2023-12-12',
    type: 'INTERMEDIATE',
  },
  {
    _id: 7,
    serialNumber: '12345678909876543',
    subject: 'Vanja Vasic',
    validFrom: '2022-05-03',
    validTo: '2023-12-12',
    type: 'INTERMEDIATE',
  },
  {
    _id: 8,
    serialNumber: '12345678909876543',
    subject: 'Vanja Vasic',
    validFrom: '2022-05-03',
    validTo: '2023-12-12',
    type: 'END',
  },
  {
    _id: 9,
    serialNumber: '12345678909876543',
    subject: 'Marija Stankov',
    validFrom: '2022-05-03',
    validTo: '2023-12-12',
    type: 'END',
  },
  {
    _id: 10,
    serialNumber: '12345678909876543',
    subject: 'Mitar Mitrovic',
    validFrom: '2022-05-03',
    validTo: '2023-12-12',
    type: 'INTERMEDIATE',
  },
  {
    _id: 11,
    serialNumber: '12345678909876543',
    subject: 'Bosko Kasic',
    validFrom: '2022-05-03',
    validTo: '2023-12-12',
    type: 'END',
  },
  {
    _id: 12,
    serialNumber: '12345678909876543',
    subject: 'Aleksandra Spasic',
    validFrom: '2022-05-03',
    validTo: '2023-12-12',
    type: 'INTERMEDIATE',
  },
];

@Injectable({
  providedIn: 'root'
})
export class CertificateService {
  private certificateList: Certificate[] = [];

  constructor(private http: HttpClient) { 
    for (let certificateObj of CERTIFICATES) {
      const certificate: Certificate = {
        _id: certificateObj._id,
        serialNumber: certificateObj.serialNumber,
        subject: certificateObj.subject,
        validFrom: certificateObj.validFrom,
        validTo: certificateObj.validTo,
        type: certificateObj.type,
      };
      this.certificateList.push(certificate);
    }
  }

  getAll(): Certificate[] {
    return this.certificateList;
  }

  add(certificate: any): void {
    this.certificateList.push(certificate);
  }

  getIssuers(): Observable<AllCertificate> {
    return this.http.get<AllCertificate>(environment.apiHost + "api/certificate/issuers");
  }

}
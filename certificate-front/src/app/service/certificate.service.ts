import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Certificate, AllCertificate } from '../domains';

@Injectable({
  providedIn: 'root'
})
export class CertificateService {
  private certificateList: Certificate[] = [];

  constructor(private http: HttpClient) { }

  add(certificate: any): void {
    this.certificateList.push(certificate);
  }

  getIssuers(): Observable<AllCertificate> {
    return this.http.get<AllCertificate>(environment.apiHost + "api/certificates/issuers");
  }

  getAll(): Observable<AllCertificate> {
    return this.http.get<AllCertificate>(environment.apiHost + 'api/certificates');
  }

  checkValidationBySerialNum(serialNumber: string) : Observable<boolean> {
    return this.http.get<boolean>(environment.apiHost + 'api/certificates/verify/' + serialNumber);
  }
}
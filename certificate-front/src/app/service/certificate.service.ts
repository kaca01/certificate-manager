import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
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

  checkValidityByCopy(file: Int8Array): Observable<boolean> {
    console.log("usao u servissss");
    console.log(file);
    const headers = new HttpHeaders()
    .set('Content-Type', 'application/json;charset=utf-8');
    let bytes = file.toString();
    console.log("FILE DTO");
    console.log(bytes)
    // .set('Access-Control-Allow-Origin', 'http://localhost:4200')
    // .set('Access-Control-Allow-Methods', 'GET,PUT,OPTIONS,POST')
    // .set('Access-Control-Allow-Headers', 'Access-Control-Allow-Origin, Content-Type, Accept, Accept-Language, Origin, User-Agent');
    return this.http.post<boolean>(environment.apiHost + 'api/certificates/verify/copy', bytes);

  }
}


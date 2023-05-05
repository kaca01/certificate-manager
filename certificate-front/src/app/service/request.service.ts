import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AllRequests, Certificate, CertificateRequest } from 'src/app/domains';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RequestService {
  private value$ = new BehaviorSubject<any>({});
  selectedValue$ = this.value$.asObservable();

  constructor(private http: HttpClient) { }

  getUserRequests(): Observable<AllRequests> {
    return this.http.get<AllRequests>(environment.apiHost + 'api/certificate-request/user');
  }
  
  getAllRequests(): Observable<AllRequests> {
    return this.http.get<AllRequests>(environment.apiHost + 'api/certificate-request/admin');
  }

  getRequestsBasedOnIssuer(): Observable<AllRequests> {
    return this.http.get<AllRequests>(environment.apiHost + 'api/certificate-request/issuer');
  }

  accept(id: number): Observable<Certificate> {
    return this.http.put<Certificate>(environment.apiHost + 'api/certificate-request/accept/' + id.toString(), {});
  }

  refuse(id: number, reason: any): Observable<CertificateRequest> {
    return this.http.put<CertificateRequest>(environment.apiHost + 'api/certificate-request/refuse/' + id.toString(), reason);
  }
}

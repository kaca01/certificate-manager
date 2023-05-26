import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CertificateRequest } from 'src/app/domains';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CertificateRequestService {

  constructor(private http: HttpClient) { }

  insert(certificateRequset: CertificateRequest, token: string) : Observable<CertificateRequest> {
    const headers = new HttpHeaders({
      'recaptcha': token,
      'Content-Type': 'application/json'
    });
    return this.http.post<CertificateRequest>(environment.apiHost + "api/certificate-request", certificateRequset, {headers});
  } 
}

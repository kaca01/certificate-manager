import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CertificateRequest } from './certificate-request.component';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CertificateRequestService {

  constructor(private http: HttpClient) { }

  insert(certificateRequset: CertificateRequest) : Observable<CertificateRequest> {
    return this.http.post<CertificateRequest>(environment.apiHost + "api/certificate-request", certificateRequset);
  } 
}

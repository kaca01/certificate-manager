import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CertificateRequest } from 'src/app/domains';
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

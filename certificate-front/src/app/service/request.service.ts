import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AllRequests } from 'src/app/domains';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RequestService {
  private value$ = new BehaviorSubject<any>({});
  selectedValue$ = this.value$.asObservable();

  constructor(private http: HttpClient) { }
  
    getAllRequests(): Observable<AllRequests> {
      return this.http.get<AllRequests>(environment.apiHost + 'api/certificate-request');
    }
}

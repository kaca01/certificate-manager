import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { ConfigService } from './config.service';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { ResetPassword, User } from '../domains';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  public currentUser : User | null = null;

  constructor(
    private apiService: ApiService,
    private config: ConfigService,
    private http: HttpClient) { }

  getMyInfo() {
    return this.apiService.get(this.config.current_user_url)
      .pipe(map(user => {
        this.currentUser = user;
        return user;
    }));
  }

  checkLogin(user:any, radio: String) : Observable<any>{
    user.verification = radio;
    return this.http.post<any>(environment.apiHost + "api/user/checkLogin", user);
  }

  login(user:any, code: String) : Observable<any>{
    user.verification = code;
    return this.http.post<any>(environment.apiHost + "api/user/login", user);
  }

  register(user: any, verification: String): Observable<User> {
    user.verification = verification;
    return this.http.post<User>(environment.apiHost + 'api/user/register', user);
  }

  
  getActivation(activationId: number): Observable<String>  {
    return this.http.get<String>(environment.apiHost + "api/user/activate/" + activationId);
  }

  sendEmail(userEmail: string): Observable<any> {
    return this.http.get<any>(environment.apiHost + 'api/user/' + userEmail + "/resetPassword");
  }

  resetPasswordViaEmail(userEmail: string, resetPassword: ResetPassword): Observable<void> {
    return this.http.put<void>(environment.apiHost + 'api/user/' + userEmail + "/resetPassword", resetPassword);
  }

  sendSMS(phone: string): Observable<void> {
    return this.http.get<void>(environment.apiHost + 'api/user/' + phone + "/sendSMS");
  }

  resetPasswordViaSMS(phone: string, resetPassword: ResetPassword): Observable<void> {
    return this.http.put<void>(environment.apiHost + 'api/user/' + phone + "/sendSMS", resetPassword);
  }
}

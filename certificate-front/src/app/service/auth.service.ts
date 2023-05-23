import { Injectable } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { ApiService } from './api.service';
import { catchError, map } from 'rxjs/operators';
import { Router } from '@angular/router';
import { ConfigService } from './config.service';
import { UserService } from './user.service';

@Injectable()
export class AuthService {

  constructor (
    private apiService: ApiService,
    private userService: UserService,
    private config: ConfigService,
    private router: Router ) {}

  private access_token = null;

  login(user:any) {
    const body = {
      'email': user.email,
      'password': user.password
    };
    return this.apiService.post(this.config.login_url, JSON.stringify(body))
      .pipe(map((res) => {
        console.log('Login success');
        this.access_token = res.body.accessToken;
        localStorage.setItem("jwt", res.body.accessToken)
      }));
  }

  checkUserSession(){
    if (localStorage.getItem("jwt") != null && (this.userService.currentUser == null || this.userService.currentUser == undefined)){
      const token = localStorage.getItem("jwt");
      this.setToken(token);
      
      console.log('Login success');
      this.userService.getMyInfo().subscribe((res:any) => {
        if(this.userService.currentUser != null) {
          this.router.navigate(['/certificate']);
        }
      })
    }
  }

  logout() {
    this.userService.currentUser = null;
    localStorage.removeItem("jwt");
    this.access_token = null;
  }

  tokenIsPresent() {
    return this.access_token != undefined && this.access_token != null;
  }

  setToken(access_token: any) {
    this.access_token = access_token;
  }

  getToken() {
    return this.access_token;
  }
}

import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Route, Router } from '@angular/router';
import axios from 'axios';
import { AuthService } from 'src/app/service/auth.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-callback',
  templateUrl: './callback.component.html',
  styleUrls: ['./callback.component.css']
})
export class CallbackComponent implements OnInit {

  constructor(private route: ActivatedRoute, private http: HttpClient, private authService: AuthService,
              private userService: UserService, private router: Router) {}

ngOnInit() {
  let token : string = "";
  this.route.queryParams.subscribe(params => {
    const code = params['code'];
  
    // Send the authorization code to the backend
    console.log("Stigao ovdje");
    this.http.post('https://localhost:8081/oauth/github', { code }, { responseType: 'text' }).subscribe(
      response => {
        // Handle success
        console.log("Nije pukao");
        console.log(response);
        token = response        
            
        console.log('Login success');
        this.getEmail(response);
      },
      error => {
        // Handle error
        console.log("Ovdje pukao");
        console.error(error);
      }
    );
  });

 
}

  getEmail(accessToken: string) {
    accessToken = accessToken.split('=')[1].split('&')[0];
    console.log(accessToken);
    console.log("usao ovdje");
    axios.get('https://api.github.com/user/emails', {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    })
      .then(response => {
        const emails: string[] = response.data;
        console.log('Emails:', emails);
      })
      .catch(error => {
        console.error('Error:', error);
      });
    }

}

export interface CodeRequest {
  code: string;
}
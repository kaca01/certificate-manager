import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatRadioChange } from '@angular/material/radio';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent implements OnInit {
  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required]),
  });

  hide: boolean = true;
  returnUrl!: string;
  submitted = false;
  notification!: DisplayMessage;

  radio : String = '';
  code: String = '';
  constructor(private router : Router, private userService: UserService, private authService: AuthService) {}

  ngOnInit(): void { 
    this.authService.logout();
    this.submitted = false;
  }

  login(): void { 
    this.notification;
    this.submitted = true;

    this.userService.checkLogin(this.loginForm.value, this.radio)
    .subscribe(data => {
        
        },
    error => {
      console.log(error);
      this.submitted = false;
      this.notification = {msgType: 'error', msgBody: 'Incorrect username or password'};
    });
  } 

  confirmLogin(){
    this.userService.login(this.loginForm.value, this.code)
    .subscribe(data => {
        localStorage.setItem("jwt", data.accessToken);
        this.authService.setToken(data.accessToken);
      
      console.log('Login success');
        this.userService.getMyInfo().subscribe((res:any) => {
          if(this.userService.currentUser != null) {
            this.router.navigate(['/certificate']);
          }
          });
        },
        error => {
          console.log(error);
          // open snackbar with message Code not valid!  is that ok???
        });
  }

  register() {
    this.router.navigate(['registration']);
  }

  radioChange(event: MatRadioChange) {
    this.radio = event.value;
  }
}

interface DisplayMessage {
  msgType: string;
  msgBody: string;
}

import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { User } from 'src/app/domains';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  registrationForm= new FormGroup({
    name: new FormControl('', [Validators.required, Validators.pattern('[a-zA-Z][a-zA-Z ]+')]),
    surname: new FormControl('', [Validators.required, Validators.pattern('[a-zA-Z][a-zA-Z ]+')]),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required]),
    repeatPassword: new FormControl('', [Validators.required]),
    phone: new FormControl('', [Validators.required, Validators.pattern('[- +()0-9]+')]),
    country: new FormControl('', [Validators.required]),
  
  }) ;

  hide : boolean = true;
  hideAgain : boolean = true;
  notification!: DisplayMessage;

  constructor(private router: Router, private service: UserService, private _snackBar: MatSnackBar) {}

  ngOnInit(): void {
      
  }

  reg() {
    if (this.registrationForm.valid) {
      if (this.registrationForm.get('password')?.value! !== this.registrationForm.get('repeatPassword')?.value!) {
        this.openSnackBar("Password is not matching!");
        return;
      }
      this.service.register(this.registrationForm.value)
      .subscribe((res: User) => {
        console.log(res);
        this.notification = {msgType: 'activation', msgBody: 'Please visit your email address to activate your account!'};
      },
      (error) => {                 
        this.handleErrors(error);
        }
      );
    }
    else this.openSnackBar("Missing data!");
  }

  login() {
    this.router.navigate(['login']);
  }

  handleErrors(error: any) {
    console.log(error);
    let e = JSON.parse(error.error);
    if(e.message!= null || e.message != undefined)  
    this.openSnackBar(e.message);
    else if(e.errors != null || e.errors != undefined)
    this.openSnackBar(e.errors);
    else this.openSnackBar("Some error occurred");
  }

  openSnackBar(snackMsg : string) : void {
    this._snackBar.open(snackMsg, "Dismiss", {
      duration: 2000
    });
  }

}

interface DisplayMessage {
  msgType: string;
  msgBody: string;

}

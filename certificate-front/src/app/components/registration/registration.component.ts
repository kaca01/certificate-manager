import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

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
    telephoneNumber: new FormControl('', [Validators.required, Validators.pattern('[- +()0-9]+')]),
    country: new FormControl('', [Validators.required]),
  
  }) ;

  hide : boolean = true;
  hideAgain : boolean = true;
  notification!: DisplayMessage;

  constructor(private router: Router) {}

  ngOnInit(): void {
      
  }

  reg() {
  }

  login() {
    this.router.navigate(['login']);
  }

}

interface DisplayMessage {
  msgType: string;
  msgBody: string;

}

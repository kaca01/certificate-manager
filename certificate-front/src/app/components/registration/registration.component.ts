import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatRadioChange } from '@angular/material/radio';
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
    password: new FormControl('', [Validators.required, Validators.minLength(8), this.createPasswordStrengthValidator()]),
    repeatPassword: new FormControl('', [Validators.required]),
    phone: new FormControl('', [Validators.required, Validators.pattern('[- +()0-9]+')]),
    country: new FormControl('', [Validators.required]),
  
  }) ;

  hide : boolean = true;
  hideAgain : boolean = true;
  notification!: DisplayMessage;
  radio : String = '';

  constructor(private router: Router, private service: UserService, private _snackBar: MatSnackBar) {}

  ngOnInit(): void {
  }

  reg() {
    if (this.registrationForm.valid) {
      if (this.registrationForm.get('password')?.value! !== this.registrationForm.get('repeatPassword')?.value!) {
        this.openSnackBar("Password is not matching!");
        return;
      }
      if (this.radio == ''){
        this.openSnackBar("Must select an option for account verification!");
        return;
      }
      this.service.register(this.registrationForm.value, this.radio)
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

  radioChange(event: MatRadioChange) {
    this.radio = event.value;
  }

  login() {
    this.router.navigate(['login']);
  }

  handleErrors(error: any) {
    console.log(error);
    if(error.error.message!= null || error.error.message != undefined)  
    this.openSnackBar(error.error.message);
    else this.openSnackBar("Some error occurred");
  }

  openSnackBar(snackMsg : string) : void {
    this._snackBar.open(snackMsg, "Dismiss", {
      duration: 2000
    });
  }

  createPasswordStrengthValidator(): ValidatorFn {
    return (control:AbstractControl) : ValidationErrors | null => {
        const value = control.value;
        if (!value) {
            return null;
        }

        const hasUpperCase = /[A-Z]+/.test(value);
        const hasLowerCase = /[a-z]+/.test(value);
        const hasNumeric = /[0-9]+/.test(value);
        const specialChars = /[`!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]/.test(value);

        const passwordValid = hasUpperCase && hasLowerCase && hasNumeric && specialChars;
        return !passwordValid ? {passwordStrength:true}: null;
    }
  }

}

interface DisplayMessage {
  msgType: string;
  msgBody: string;
}

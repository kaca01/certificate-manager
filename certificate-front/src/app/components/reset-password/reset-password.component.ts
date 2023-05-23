import { Component } from '@angular/core';
import { HttpErrorResponse } from "@angular/common/http";
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router'; 
import { ResetPassword } from 'src/app/domains';
import { UserService } from 'src/app/service/user.service';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent {

  emailForm = new FormGroup({
    email: new FormControl('', [Validators.required])
  });

  resetPasswordForm = new FormGroup({
    code: new FormControl('', [Validators.required]),
    newPassword: new FormControl('', [Validators.required,  Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/)]),
    firstRepetedPassword: new FormControl('', [Validators.required,  Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/)]),
    secondRepetedPassword: new FormControl('', [Validators.required,  Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/)])
  });
  
  hide1: boolean = true;
  hide2: boolean = true;
  hide3: boolean = true;
  isSendEmail: boolean = false;

  resetPassword = {} as ResetPassword;
  email: string = "";

  expiredPassword: boolean = false;

  constructor(private userService: UserService, private _snackBar: MatSnackBar, private router: Router, private authService: AuthService) {
    this.expiredPassword = userService.isExpiredPassword();
    this.authService.logout();
  }

  first() : boolean {
    if(this.resetPasswordForm.get('newPassword')!.value !== this.resetPasswordForm.get('firstRepetedPassword')!.value) 
      return true    
    return false
  }

  second() : boolean {
    if(this.resetPasswordForm.get('newPassword')!.value !== this.resetPasswordForm.get('secondRepetedPassword')!.value) 
      return true    
    return false
  }

  openSnackBar(snackMsg : string) : void {
    this._snackBar.open(snackMsg, "Dismiss", {
      duration: 2000
    });
  }

  checkEmail(): boolean {
    this.isSendEmail = true;
    return true;
  }

  sendCode() : void {
    const phoneRegex = new RegExp('[- +()0-9]+');
    const emailRegex = new RegExp('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$');

    if(this.emailForm.controls['email'].value != '') {
      this.email = this.emailForm.controls['email']?.value!;
      if(!emailRegex.test(this.email) && !phoneRegex.test(this.email))
        this.openSnackBar("Invalid email/phone format");
      else {
        if(emailRegex.test(this.email)) 
          this.userService.sendEmail(this.email).subscribe()
        else 
          this.userService.sendSMS(this.email).subscribe()
        this.openSnackBar("A verification code has been sent!");
        this.checkEmail();
      }   
    }      
  }

  doResetPassword() : void {
    if(this.resetPasswordForm.controls['newPassword'].value != '' && 
        this.resetPasswordForm.controls['code'].value != '' &&
        !this.first() && 
        !this.second()) {
        if(this.resetPasswordForm.controls['newPassword'].errors == null) {
          this.resetPassword['newPassword'] = this.resetPasswordForm.controls['newPassword']?.value!;
          this.resetPassword['firstRepetedPassword'] = this.resetPasswordForm.controls['firstRepetedPassword']?.value!;
          this.resetPassword['secondRepetedPassword'] = this.resetPasswordForm.controls['secondRepetedPassword']?.value!;
          this.resetPassword['code'] = this.resetPasswordForm.controls['code']?.value!;

          console.log(this.email)
          if(this.email.includes('@')) {
            this.userService.resetPasswordViaEmail(this.email, this.resetPassword).subscribe(
              () => {
              this.openSnackBar("Successfully reset password!");
              this.authService.logout();
              this.router.navigate(['login']);
            },
              (error: HttpErrorResponse) => {
                console.log(error)
                this.openSnackBar(error.error['message']);
            })
          }
            
          else {
            this.userService.resetPasswordViaSMS(this.email, this.resetPassword).subscribe(
              () => {
              this.openSnackBar("Successfully reset password!");
              this.authService.logout();
              this.router.navigate(['login']);
            },
              (error: HttpErrorResponse) => {
                console.log(error)
                this.openSnackBar(error.error['message']);
            }) 
          }
      }
    }
  }
}

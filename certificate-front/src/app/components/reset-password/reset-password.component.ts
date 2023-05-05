import { Component } from '@angular/core';
import { HttpErrorResponse } from "@angular/common/http";
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router'; 
import { ResetPassword } from 'src/app/domains';
import { UserService } from 'src/app/service/user.service';
import { AuthService } from 'src/app/service/auth.service';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent {

  emailForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email])
  });

  resetPasswordForm = new FormGroup({
    newPassword: new FormControl('', [Validators.required]),
    code: new FormControl('', [Validators.required])
  });

  hide: boolean = true;
  isSendEmail: boolean = false;

  resetPassword = {} as ResetPassword;
  email: string = "";

  constructor(private userService: UserService, private _snackBar: MatSnackBar, private router: Router,
     private authService: AuthService) {}

  openSnackBar(snackMsg : string) : void {
    this._snackBar.open(snackMsg, "Dismiss", {
      duration: 2000
    });
  }

  sendEmail() {
    if(this.emailForm.controls['email'].value != '') {
      this.email = this.emailForm.controls['email']?.value!;
      this.userService.sendEmail(this.email).subscribe((res:any) => {
        this.openSnackBar("A verification code has been sent to your email!");
        this.checkEmail();
      },
        (error: HttpErrorResponse) => {
          this.openSnackBar(error.error);
      })
    }
  }

  checkEmail(): boolean {
    this.isSendEmail = true;
    return true;
  }

  doResetPassword() {
    if(this.resetPasswordForm.controls['newPassword'].value != '' && 
        this.resetPasswordForm.controls['code'].value != '') {
          this.resetPassword['newPassword'] = this.resetPasswordForm.controls['newPassword']?.value!;
          this.resetPassword['code'] = this.resetPasswordForm.controls['code']?.value!;

          this.userService.resetPassword(this.email, this.resetPassword)
      .subscribe(
        () => {
        this.openSnackBar("Successfully reset password!");
        this.authService.logout();
        this.router.navigate(['login']);
      },
        (error: HttpErrorResponse) => {
          this.openSnackBar("Code is expired or not correct!");
      })
    }
  }
}

import { Component, Inject, OnInit } from '@angular/core';
import { RequestsComponent } from '../requests.component';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from 'src/app/service/user.service';
import { RequestService } from 'src/app/service/request.service';
import { CertificateRequest, RequestReason } from 'src/app/domains';

@Component({
  selector: 'app-add-reason-dialog',
  templateUrl: './add-reason-dialog.component.html',
  styleUrls: ['./add-reason-dialog.component.css']
})
export class AddReasonDialogComponent {

  private requests = {} as RequestsComponent;
  private requestNote = {} as RequestReason;
  message = "";
  constructor(private requestService : RequestService, private _snackBar: MatSnackBar, private dialogRef: MatDialogRef<AddReasonDialogComponent>,
    @Inject(MAT_DIALOG_DATA) data: any) {
      this.requests = data;
    }

  close() : void {
    this.dialogRef.close();
  }

  save() : void {
    this.message = this.message.trim();
    if(this.message != '') {
      this.requestNote["message"] = this.message;
      this.requestService.refuse(this.requests.request.id, this.requestNote)
      .subscribe((res: CertificateRequest) => {
        console.log(res);
        this.openSnackBar("Successfully refused the request!");
      },
        (error: HttpErrorResponse) => {
          this.handleErrors(error);
      }
    );
    }else{
      this.openSnackBar("Note should not be empty!");
    }
    this.dialogRef.close();
  }

  openSnackBar(snackMsg : string) : void {
    this._snackBar.open(snackMsg, "Dismiss", {
      duration: 2000
    });
  }

  handleErrors(error: any) {
    console.log(error);
    if(error.error.message!= null || error.error.message != undefined)  
    this.openSnackBar(error.error.message);
    else this.openSnackBar("Some error occurred");
  }

}

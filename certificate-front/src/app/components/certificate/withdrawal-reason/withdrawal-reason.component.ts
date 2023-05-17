import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Certificate } from 'src/app/domains';
import { CertificateService } from 'src/app/service/certificate.service';

@Component({
  selector: 'app-withdrawal-reason',
  templateUrl: './withdrawal-reason.component.html',
  styleUrls: ['./withdrawal-reason.component.css']
})
export class WithdrawalReasonComponent implements OnInit {
  private certificate: Certificate = {} as Certificate;
  public reason: string = "";

  constructor(private dialogRef: MatDialogRef<WithdrawalReasonComponent>, private snackBar: MatSnackBar, private service: CertificateService,
    @Inject(MAT_DIALOG_DATA) data: Certificate) { 
      this.certificate = data;
    }

  ngOnInit(): void {
  }

  withdraw() : void {
    this.service.invalidate(this.certificate.serialNumber, this.reason).subscribe(
      (res) => {
      this.openSnackBar("Withdrawal successfull!");    
      },
      (error) => {
        this.openSnackBar(error.error.message);
      });
    this.dialogRef.close();
  }

  close() : void {
    this.dialogRef.close();
  }

  openSnackBar(snackMsg : string, duration = 2000) : void {
    this.snackBar.open(snackMsg, "Dismiss", {
      duration: duration
    });
  }

}

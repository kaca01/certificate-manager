import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Certificate } from 'src/app/domains';

@Component({
  selector: 'app-withdrawal-reason',
  templateUrl: './withdrawal-reason.component.html',
  styleUrls: ['./withdrawal-reason.component.css']
})
export class WithdrawalReasonComponent implements OnInit {
  private certificate: Certificate = {} as Certificate;

  constructor(private dialogRef: MatDialogRef<WithdrawalReasonComponent>, private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) data: Certificate) { 
      this.certificate = data;
    }

  ngOnInit(): void {
  }

  withdraw() : void {

  }

  close() : void {
    this.dialogRef.close();
  }

}

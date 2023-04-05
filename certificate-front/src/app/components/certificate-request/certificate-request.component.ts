import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-certificate-request',
  templateUrl: './certificate-request.component.html',
  styleUrls: ['./certificate-request.component.css']
})
export class CertificateRequestComponent implements OnInit {

  constructor(private dialogRef: MatDialogRef<CertificateRequestComponent>, 
    @Inject(MAT_DIALOG_DATA) data: any) {

     }

  ngOnInit(): void {
  }

  save():void {

  }

  close(): void{
    this.dialogRef.close();
  }

}

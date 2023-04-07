import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Certificate } from '../certificate/certificate.component';
import { CertificateService } from 'src/app/service/certificate.service';

@Component({
  selector: 'app-certificate-request',
  templateUrl: './certificate-request.component.html',
  styleUrls: ['./certificate-request.component.css']
})
export class CertificateRequestComponent implements OnInit {
  issuers: Certificate[] = [];

  constructor(private dialogRef: MatDialogRef<CertificateRequestComponent>, private certificateService: CertificateService,
    @Inject(MAT_DIALOG_DATA) data: any) {

     }

  ngOnInit(): void {
    this.certificateService.getIssuers().subscribe((res)=> {
      this.issuers = res;
      console.log("ISSUERS");
      console.log(this.issuers);
    });
  }

  save():void {

  }

  close(): void{
    this.dialogRef.close();
  }

}

export interface CertificateRequest {
    requestType: string,
    issuer: number,
    certificateType: string,
    subject: number,
    refusalReason: number
}

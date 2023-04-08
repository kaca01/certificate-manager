import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AllCertificate, Certificate } from '../certificate/certificate.component';
import { CertificateService } from 'src/app/service/certificate.service';
import { CertificateRequestService } from './certificate-request.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-certificate-request',
  templateUrl: './certificate-request.component.html',
  styleUrls: ['./certificate-request.component.css']
})
export class CertificateRequestComponent implements OnInit {
  issuers: AllCertificate = {} as AllCertificate;
  issuer: string = "";
  type: string = "";

  constructor(private dialogRef: MatDialogRef<CertificateRequestComponent>, private certificateService: CertificateService,
              private certificateRequsetService: CertificateRequestService, private userService: UserService,
              @Inject(MAT_DIALOG_DATA) data: any) {

     }

  ngOnInit(): void {
    this.certificateService.getIssuers().subscribe((res)=> {
      this.issuers = res;
    });
  }

  save():void {
    console.log("PRINTTTTTTT");
    console.log(this.type);
    console.log(this.issuer);
    console.log(this.userService.currentUser?.id);
  }

  close(): void{
    this.dialogRef.close();
  }

}

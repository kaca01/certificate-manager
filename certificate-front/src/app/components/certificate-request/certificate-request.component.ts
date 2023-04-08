import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AllCertificate, Certificate } from '../certificate/certificate.component';
import { CertificateService } from 'src/app/service/certificate.service';
import { CertificateRequestService } from './certificate-request.service';
import { UserService } from 'src/app/service/user.service';
import { CertificateRequest } from 'src/app/domains';
import { MatSnackBar } from '@angular/material/snack-bar';

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
              private certificateRequsetService: CertificateRequestService, private userService: UserService, private snackBar: MatSnackBar,
              @Inject(MAT_DIALOG_DATA) data: any) {

     }

  ngOnInit(): void {
    this.certificateService.getIssuers().subscribe((res)=> {
      this.issuers = res;
    });

  }

  save(): void {
    if (this.issuer == "" || this.type == "") {
      this.openSnackBar("No empty fields are allowed!");
      return;
    }
    let certifcateRequest: CertificateRequest = {} as CertificateRequest;
    certifcateRequest.certificateType = this.type;
    certifcateRequest.issuer = this.issuer;
    certifcateRequest.requestType = "ACTIVE";
    if (this.userService.currentUser?.id != undefined) certifcateRequest.subject = this.userService.currentUser?.id;

    this.certificateRequsetService.insert(certifcateRequest).subscribe((res)=> {
      this.openSnackBar("Successfully added!");
    });

    this.dialogRef.close();
  }

  close(): void{
    this.dialogRef.close();
  }

  openSnackBar(snackMsg : string) : void {
    this.snackBar.open(snackMsg, "Dismiss", {
      duration: 2000
    });
  }

}

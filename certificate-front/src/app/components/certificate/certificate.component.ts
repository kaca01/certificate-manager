import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Certificate } from 'src/app/domains';
import { CertificateService } from 'src/app/service/certificate.service';
import { CertificateRequestComponent } from '../certificate-request/certificate-request.component';
import { UserService } from 'src/app/service/user.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FileUploadDialogComponent } from './file-upload-dialog/file-upload-dialog.component';
import { WithdrawalReasonComponent } from './withdrawal-reason/withdrawal-reason.component';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'certificate',
  templateUrl: './certificate.component.html',
  styleUrls: ['./certificate.component.css']
})
export class CertificateComponent implements OnInit {
  selectedRowIndex : number = -1;
  displayedColumns: string[] = ['serial number', 'subject', 'valid from', 'valid to', 'type'];
  dataSource!: MatTableDataSource<Certificate>;

  certificates: Certificate[] = [];
  private certificate = {} as Certificate;

  @ViewChild(MatPaginator) paginator!: any;
  @ViewChild(MatSort) sort!: any;

  constructor(private certificateService: CertificateService, private dialog: MatDialog, private snackBar: MatSnackBar, 
    private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.checkUserSession();
    this.certificateService.getAll().subscribe((res) => {
      for(let i = 0; i<res.totalCount; i++) {
        res.results[i]._id = i+1;
      }
      this.certificates = res.results;
      this.dataSource = new MatTableDataSource<Certificate>(this.certificates);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    });
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  openDialog() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    
    this.dialog.open(CertificateRequestComponent, dialogConfig);
  }

  openFileUploadDialog() : void {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    
    this.dialog.open(FileUploadDialogComponent, dialogConfig);
  }

  getCertificate(cer : Certificate) {
    this.selectedRowIndex = cer._id;
    this.certificate = cer;
    const Menu = document.getElementById("menu-container");
    if(Menu != null) Menu.style.display = 'none';
  }

  checkValidationById() : void {
    if(this.selectedRowIndex === -1) 
      this.openSnackBar("Certificate not selected!")
    else {
      this.certificateService.checkValidationBySerialNum(this.certificate.serialNumber).subscribe((res) => {
        if(res) 
          this.openSnackBar('The selected certificate is valid');
        else
          this.openSnackBar('The selected certificate is not valid');
      });
    }
  }

  openSnackBar(snackMsg : string) : void {
    this.snackBar.open(snackMsg, "Dismiss", {
      duration: 2000
    });
  }

  openWithdrawalDialog() : void {
    if (this.selectedRowIndex === -1) {
      this.openSnackBar("Certificate not selected!");
      return;
    }

    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.data = this.certificate;
    
    this.dialog.open(WithdrawalReasonComponent, dialogConfig);
  }

  downloadPrivateKey() : void {
    if (this.selectedRowIndex === -1) {
      this.openSnackBar("Certificate not selected!");
      return;
    }

    this.certificateService.downloadPk(this.certificate.serialNumber).subscribe((res) => {
      const blob = new Blob([res], {type: 'application/octet-stream'});
      const blobUrl = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = blobUrl;
      link.download = this.certificate.serialNumber + '.p12';
      link.dispatchEvent(new MouseEvent('click'));
    },
    (error) => {               
      this.openSnackBar("You are not owner of this certificate!");
      }
    );

  }

  downloadCertificate() : void {
    if (this.selectedRowIndex === -1) {
      this.openSnackBar("Certificate not selected!");
      return;
    }

    this.certificateService.downloadCert(this.certificate.serialNumber).subscribe((res) => {
        const blob = new Blob([res], {type: 'application/octet-stream'});
        const blobUrl = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = blobUrl;
        link.download = this.certificate.serialNumber + '.cer';
        link.dispatchEvent(new MouseEvent('click'));
    },
    (error) => {                 
      this.openSnackBar("Some error ocurred");
      }
    );

  }
}

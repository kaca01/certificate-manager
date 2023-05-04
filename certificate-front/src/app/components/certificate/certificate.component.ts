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

@Component({
  selector: 'certificate',
  templateUrl: './certificate.component.html',
  styleUrls: ['./certificate.component.css']
})
export class CertificateComponent implements OnInit {
  selectedRowIndex : number = -1;
  displayedColumns: string[] = ['serial number', 'subject', 'valid from', 'valid to', 'type', 'download'];
  dataSource!: MatTableDataSource<Certificate>;

  certificates: Certificate[] = [];
  private certificate = {} as Certificate;

  @ViewChild(MatPaginator) paginator!: any;
  @ViewChild(MatSort) sort!: any;

  constructor(private router: Router, private certificateService: CertificateService, private dialog: MatDialog, private userService: UserService) {}

  ngOnInit(): void {
    if (this.userService.currentUser == undefined || this.userService.currentUser == null)
      this.router.navigate(['/welcome-page']);
    this.certificates = this.certificateService.getAll();
    this.dataSource = new MatTableDataSource<Certificate>(this.certificates);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
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

  getCertificate(cer : Certificate) {
    this.selectedRowIndex=cer._id;
    this.certificate = cer;
    const Menu = document.getElementById("menu-container");
    if(Menu != null) Menu.style.display = 'none';
  }
}

// export interface Certificate {
//   _id: number;
//   serialNumber: string;
//   subject: string;
//   validFrom: string;
//   validTo: string;
//   type: string;
//   }

// export interface AllCertificate {
//   totalCount: number;
//   results: Certificate[];
// }

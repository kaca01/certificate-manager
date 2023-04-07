import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Certificate } from 'src/app/domains';
import { CertificateService } from 'src/app/service/certificate.service';

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

  constructor(private certificateService: CertificateService) {}

  ngOnInit(): void {
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

  getCertificate(cer : Certificate) {
    this.selectedRowIndex=cer._id;
    this.certificate = cer;
    const Menu = document.getElementById("menu-container");
    if(Menu != null) Menu.style.display = 'none';
  }
}

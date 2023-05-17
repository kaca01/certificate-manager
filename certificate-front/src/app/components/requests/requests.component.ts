import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { RequestService } from 'src/app/service/request.service';
import { CertificateRequest } from 'src/app/domains';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-requests',
  templateUrl: './requests.component.html',
  styleUrls: ['./requests.component.css']
})
export class RequestsComponent implements OnInit {
  selectedRowIndex : number = -1;
  displayedColumns: string[] = ['date', 'issuer', 'subject', 'type', 'status'];
  dataSource!: MatTableDataSource<CertificateRequest>;
  valueFromCreateComponent = '';

  all: CertificateRequest[] = [];
  private request = {} as CertificateRequest;

  user!: string;

  @ViewChild(MatPaginator) paginator!: any;
  @ViewChild(MatSort) sort!: any;

  constructor(private requestService: RequestService, private userService: UserService) { }

  ngOnInit(): void {
    this.whoIsUser();

    if(this.user === "user") {
      this.requestService.getUserRequests().subscribe((res) => {
        for(let i = 0; i<res.totalCount; i++) {
          res.results[i]._id = i+1;
        }
        this.all = res.results;
        this.dataSource = new MatTableDataSource<CertificateRequest>(this.all);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      });
    }

    else if(this.user === "admin") {
      this.requestService.getAllRequests().subscribe((res) => {
        for(let i = 0; i<res.totalCount; i++) {
          res.results[i]._id = i+1;
        }
        this.all = res.results;
        this.dataSource = new MatTableDataSource<CertificateRequest>(this.all);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      });
    }
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  getRequest(request : CertificateRequest) {
    this.selectedRowIndex=request._id;
    this.request = request;
    const Menu = document.getElementById("menu-container");
    if(Menu != null) Menu.style.display = 'none';
  }

  refuse(){

  }

  accept(){

  }

  whoIsUser(): string {
		if(this.userService.currentUser?.roles != undefined) {
			if(this.userService.currentUser?.roles.find(x => x.authority === "ROLE_USER")) 
				return this.user = "user";
			else if(this.userService.currentUser?.roles.find(x => x.authority === "ROLE_ADMIN")) 
			  return this.user = "admin";
		}
		return this.user = "none";
	}
}

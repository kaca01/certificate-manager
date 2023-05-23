import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { CertificateRequest } from 'src/app/domains';
import { AuthService } from 'src/app/service/auth.service';
import { RequestService } from 'src/app/service/request.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {

  selectedRowIndex : number = -1;
  displayedColumns: string[] = ['issuer', 'subject', 'type', 'status'];
  dataSource!: MatTableDataSource<CertificateRequest>;
  valueFromCreateComponent = '';

  all: CertificateRequest[] = [];
  private request = {} as CertificateRequest;

  user!: string;

  @ViewChild(MatPaginator) paginator!: any;
  @ViewChild(MatSort) sort!: any;

  constructor(private router: Router, private requestService: RequestService, private userService: UserService,
      private authService: AuthService) { }

  ngOnInit(): void {
    this.authService.checkUserSession();
    this.whoIsUser();

    if(this.user === "user") {
      this.requestService.getUserRequests().subscribe((res) => {
        for(let i = 0; i<res.totalCount; i++) {
          res.results[i]._tableId = i+1;
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
          res.results[i]._tableId = i+1;
        }
        this.all = res.results;
        this.dataSource = new MatTableDataSource<CertificateRequest>(this.all);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      });
    } else{
      this.router.navigate(["welcome-page"]);
    }
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
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

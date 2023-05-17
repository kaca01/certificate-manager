import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { RequestService } from 'src/app/service/request.service';
import { Certificate, CertificateRequest } from 'src/app/domains';
import { UserService } from 'src/app/service/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { AddReasonDialogComponent } from './add-reason-dialog/add-reason-dialog.component';

@Component({
  selector: 'app-requests',
  templateUrl: './requests.component.html',
  styleUrls: ['./requests.component.css']
})
export class RequestsComponent implements OnInit {
  selectedRowIndex : number = -1;
  displayedColumns: string[] = ['issuer', 'subject', 'type', 'status'];
  dataSource!: MatTableDataSource<CertificateRequest>;
  valueFromCreateComponent = '';

  all: CertificateRequest[] = [];
  public request = {} as CertificateRequest;

  user!: string;

  @ViewChild(MatPaginator) paginator!: any;
  @ViewChild(MatSort) sort!: any;

  constructor(private router: Router, private requestService: RequestService, private userService: UserService, private _snackBar: MatSnackBar, private dialog: MatDialog) { }

  ngOnInit(): void {
    this.requestService.getRequestsBasedOnIssuer().subscribe((res) => {
      for(let i = 0; i<res.totalCount; i++) {
        res.results[i]._tableId = i+1;
      }
      this.all = res.results;
      this.dataSource = new MatTableDataSource<CertificateRequest>(this.all);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    });
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  getRequest(request : CertificateRequest) {
    this.selectedRowIndex=request._tableId;
    this.request = request;
    const Menu = document.getElementById("menu-container");
    if(Menu != null) Menu.style.display = 'none';
  }

  refuse(){
    if (!this.checkIfSelected()) return;
    if (this.request.requestType != "ACTIVE"){
      this.openSnackBar("Cannot refuse/accept request that is no longer active");
      return;
    }
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.data = this;

    const dialogRef = this.dialog.open(AddReasonDialogComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(() => {
        this.selectedRowIndex = -1;
        this.ngOnInit();
      });
  }

  accept(){
    if (!this.checkIfSelected()) return;
    this.requestService.accept(this.request.id).subscribe((res: Certificate) => {
      console.log(res);
      this.openSnackBar("Request successfully accepted!");
      this.ngOnInit();
    },
    (error) => {                 
      this.handleErrors(error);
      }
    );
  }

  private checkIfSelected() : boolean {
    if(this.selectedRowIndex==-1){
      this.openSnackBar("Request not selected!");
      return false;
    }
    return true;
  }
  
  handleErrors(error: any) {
    if (error.error){
      let e = error.error;
      console.log(e);
      if(e.message!= null || e.message != undefined)  
          return this.openSnackBar(e.message);
      else if(e.errors != null || e.errors != undefined)
          return this.openSnackBar(e.errors);
      else this.openSnackBar("Some error occurred");
}}

  whoIsUser(): string {
		if(this.userService.currentUser?.roles != undefined) {
			if(this.userService.currentUser?.roles.find(x => x.authority === "ROLE_USER")) 
				return this.user = "user";
			else if(this.userService.currentUser?.roles.find(x => x.authority === "ROLE_ADMIN")) 
			  return this.user = "admin";
		}
		return this.user = "none";
	}

  openSnackBar(snackMsg : string) : void {
    this._snackBar.open(snackMsg, "Dismiss", {
      duration: 2000
    });
  }
}

import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { RequestService } from 'src/app/service/request.service';

@Component({
  selector: 'app-requests',
  templateUrl: './requests.component.html',
  styleUrls: ['./requests.component.css']
})
export class RequestsComponent implements OnInit {

  selectedRowIndex : number = -1;
  displayedColumns: string[] = ['issuer', 'subject', 'type', 'status'];
  dataSource!: MatTableDataSource<Request>;
  valueFromCreateComponent = '';

  all: Request[] = [];
  private request = {} as Request;

  @ViewChild(MatPaginator) paginator!: any;
  @ViewChild(MatSort) sort!: any;

  constructor(private router: Router, private requestService: RequestService) { }

  ngOnInit(): void {
    // this.requestService.selectedValue$.subscribe((value) => {
    //   this.valueFromCreateComponent = value;
    // });

    // this.requestService.getAllPassengers().subscribe((res) => {
    //   this.all = res.results;
    //   this.dataSource = new MatTableDataSource<Request>(this.all);
    //   this.dataSource.paginator = this.paginator;
    //   this.dataSource.sort = this.sort;
    // });
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  getRequest(request : Request) {
    //this.selectedRowIndex=request.id;
    this.request = request;
  }

  refuse(){

  }

  accept(){

  }
}

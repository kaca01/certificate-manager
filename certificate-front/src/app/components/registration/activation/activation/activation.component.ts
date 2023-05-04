import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-activation',
  templateUrl: './activation.component.html',
  styleUrls: ['./activation.component.css']
})
export class ActivationComponent implements OnInit {

  @ViewChild('message') message!: ElementRef;
  activationId!: string | null;

  constructor(private service: UserService, private activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
      this.activationId = this.activatedRoute.snapshot.paramMap.get('id');
      this.service.getActivation(parseInt(this.activationId!))
      .subscribe((res: any) => {
        const p = document.getElementById("message");
        p!.innerHTML = "Your account has been successfully activated. You can login now!";
        const login = document.getElementById("login");
        login!.innerHTML = "Login";
        }, (error) => { 
          console.log(error);
            const errorTxt = this.handleErrors(error);
            const p = document.getElementById("message");
            p!.innerHTML = errorTxt;
        })
  }

  handleErrors(error: any) : string {
    if (error.error){
        return error.error;
    }
    else{
        let e = JSON.parse(error.error);
        if(e.message!= null || e.message != undefined)  
            return e.message;
        else if(e.errors != null || e.errors != undefined)
            return e.errors;
        else return "Some error occurred";
  }}
}

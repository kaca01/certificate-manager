import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-callback',
  templateUrl: './callback.component.html',
  styleUrls: ['./callback.component.css']
})
export class CallbackComponent implements OnInit {

  constructor(private route: ActivatedRoute, private http: HttpClient) {}

ngOnInit() {
  this.route.queryParams.subscribe(params => {
    const code = params['code'];

    // Send the authorization code to the backend
    console.log("Stigao ovdje");
    this.http.post('https://localhost:8081/oauth/github', { code }).subscribe(
      response => {
        // Handle success
        console.log("Nije pukao");
        console.log(response);
      },
      error => {
        // Handle error
        console.log("Ovdje pukao");
        console.error(error);
      }
    );
  });
}

}

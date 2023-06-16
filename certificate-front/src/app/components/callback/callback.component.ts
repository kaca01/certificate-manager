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

//   this.route.queryParams.subscribe(params0 => {
//     const code = params0['code'];
//     let codeRequest : CodeRequest = {} as CodeRequest;
//     codeRequest.code = code;
//     const params = new URLSearchParams();
//     params.set('client_id', 'd9d88e021cc55fe85e59');
//     params.set('client_secret', 'cb3df518dcc8ab4827586b1f37d416acd720608c');
//     params.set('code', code);
//     let redirectUri : string = "https://localhost:4200/oauth/callback"; // Your callback URL

//     params.set('redirect_uri', redirectUri);
    

//     this.http.post('https://localhost:8081/oauth/github', { code }, {
//       headers: { 'Content-Type': 'application/json' }
//     }).subscribe(
//       response => {
//         // Handle success
//         console.log(response);
//       },
//       error => {
//         // Handle error
//         console.error(error);
//       }
//     );
// }
  
    // Use the `code` value as needed
  // );
}

}

export interface CodeRequest {
  code: string;
}
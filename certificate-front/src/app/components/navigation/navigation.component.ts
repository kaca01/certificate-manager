import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent implements OnInit {

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
  }

  userName() {
    return "Sara";
  }

  logOut() {
    this.authService.logout();
  }

}

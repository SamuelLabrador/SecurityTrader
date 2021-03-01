import { Component } from '@angular/core';
import { ApiService } from './services/api.service';
import { AuthService } from "./services/auth.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent {

  constructor (private apiService: ApiService,
               private authService: AuthService) {}

  ngOnInit() {
  }

  isLoggedIn = this.authService.isLoggedIn();
  logout = this.authService.logout();

  /**
   * This function gets the status of the server
   */
  async getStatus() {
    const result = await this.apiService.getStatus();
  }
}

import { Component } from '@angular/core';
import { ApiService } from './services/api.service';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent {

  constructor (private apiService: ApiService) {}

  ngOnInit() {
    this.getStatus();
  }

  /**
   * This function gets the status of the server
   */
  async getStatus() {
    const result = await this.apiService.getStatus();
  }
}

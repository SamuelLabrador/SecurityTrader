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

  /**
   * When Create Game button is clicked, this function
   * handles the event
   */
  createGame() {
    // TODO: Replace this with proper logging service
    console.log('Creating game');
  }

  /**
   * When Join Game button is clicked, this function
   * handles the event
   */
  joinGame() {
    // TODO: Replace this with proper logging service
    console.log('Joining game');
  }
}

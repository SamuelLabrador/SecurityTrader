import { Component } from '@angular/core';
import { ApiService } from './services/api.service';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent {
  title = 'webapp';

  constructor (private apiService: ApiService) {}

  ngOnInit() {
    this.getStatus();
  }

  async getStatus() {
    const result = await this.apiService.getStatus();
  }
}

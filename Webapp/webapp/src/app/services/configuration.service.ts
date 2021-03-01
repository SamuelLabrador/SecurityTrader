import { Injectable } from '@angular/core';
import { AuthService } from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class ConfigurationService {
  production: Boolean;
  serverUrl: string;
  webSocketUrl: string;

  constructor() {
    this.production = false;
    this.serverUrl = 'http://localhost:9000';
    this.webSocketUrl = 'ws://localhost:9000/ws';

  }
}

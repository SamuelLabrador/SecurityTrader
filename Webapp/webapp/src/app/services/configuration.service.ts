import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ConfigurationService {
  production: Boolean;
  serverUrl: string;
  wsUrl: string;

  constructor() {
    this.production = false;
    this.serverUrl = 'https://localhost:9443';
    this.wsUrl = 'ws://localhost:9443/ws';
  }
}

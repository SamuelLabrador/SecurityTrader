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
    this.serverUrl = 'http://localhost:9000';
    this.wsUrl = 'ws://localhost:9000/ws';
  }
}

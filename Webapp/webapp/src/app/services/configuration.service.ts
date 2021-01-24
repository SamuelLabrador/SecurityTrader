import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ConfigurationService {
  production: Boolean;
  serverUrl: string;

  constructor() { 
    this.production = false;
    this.serverUrl = 'http://localhost:9000';
  }
}

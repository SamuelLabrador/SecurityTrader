import { Injectable } from '@angular/core';
import { RoutesApi, Configuration, ModelsRestServerStatus } from 'security_trader_api';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  api: RoutesApi

  constructor() {
    const config = new Configuration({
      basePath: 'http://localhost:9000'
    });

    this.api = new RoutesApi(config);
  }

  async getStatus(): Promise<ModelsRestServerStatus> {
    return this.api.status();
  }
}

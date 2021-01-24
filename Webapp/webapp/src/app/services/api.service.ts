import { Injectable } from '@angular/core';
import { RoutesApi, Configuration, ModelsRestServerStatus } from 'security_trader_api';
import { ConfigurationService } from './configuration.service';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  api: RoutesApi

  constructor(applicationConfig: ConfigurationService) {
    const config = new Configuration({
      basePath: applicationConfig.serverUrl
    });

    this.api = new RoutesApi(config);
  }

  public getStatus(): Promise<ModelsRestServerStatus> {
    return this.api.status();
  }
}

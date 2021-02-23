import { Injectable } from '@angular/core';
import { ConfigurationService } from './configuration.service';
import { RoutesApi, Configuration, ModelsRestServerStatus } from '../fetch/api';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  api: RoutesApi;

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

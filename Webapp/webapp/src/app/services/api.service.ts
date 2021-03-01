import { Injectable } from '@angular/core';
import { ConfigurationService } from './configuration.service';
import {
  RoutesApi,
  Configuration,
  ModelsRestServerStatus,
  ModelsRestAuthResponse,
  ModelsRestLoginRequest, ModelsRestCreateRequest
} from '../fetch/api';

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

  public login(email: string, password: string): Promise<ModelsRestAuthResponse> {
    const body = {
      email,
      password
    } as ModelsRestLoginRequest;

    return this.api.userLogin(body);
  }

  public createUser(email: string, username: string, password: string): Promise<ModelsRestAuthResponse> {
    const body = {
      email,
      username,
      password
    } as ModelsRestCreateRequest;

    return this.api.createUser(body);
  }
}

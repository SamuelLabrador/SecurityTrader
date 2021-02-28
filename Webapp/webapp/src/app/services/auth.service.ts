import { Injectable } from '@angular/core';
import jwt_decode from 'jwt-decode';
import {ModelsRestPlayer} from "../fetch/api";

export interface SecurityTraderToken {
  exp: number;
  iat: number;
  id: string;
  email: string;
  username: string;
  isMod: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private token: string = '';
  private tokenData: SecurityTraderToken | undefined = undefined;

  constructor() { }

  /**
   * Gets the token string
   */
  getToken(): String {
    return this.token;
  }

  /**
   * Gets the parsed token data
   */
  getTokenData(): SecurityTraderToken | undefined {
    return this.tokenData;
  }

  /**
   * Gets the parsed user data from the token data
   */
  getUserData(): ModelsRestPlayer | undefined {
    const result = this.tokenData ? this.tokenData as ModelsRestPlayer : undefined;
    return result;
  }

  /**
   * This method should be invoked upon a successful login.
   * ie:
   *
   *  sampleLogin(email, password) {
   *    const response = await this.apiService.login(email, password);
   *    this.authService.setToken(response.token);
   *  }
   *
   * @param token The token returned by the server
   */
  setToken(token: string) {
    try {
      const tokenInfo = jwt_decode(token) as SecurityTraderToken;
      this.tokenData = tokenInfo;
    } catch (e) {
      console.log(e);
    }
  }
}

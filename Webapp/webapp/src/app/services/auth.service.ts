import { Injectable } from '@angular/core';
import jwt_decode from 'jwt-decode';
import {ModelsRestPlayer} from "../fetch/api";
import { ApiService } from "./api.service";

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

  constructor(private apiService: ApiService) {
    const maybeToken = localStorage.getItem("token");
    if (maybeToken) {
      this.setToken(maybeToken);
    }
  }

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
      // Check if token is valid first.
      const tokenInfo = jwt_decode(token) as SecurityTraderToken;
      this.tokenData = tokenInfo;

      // Save token to localStorage. We want to use token in WebSocket messages
      localStorage.setItem('token', token);
    } catch (e) {
      console.log(e);
    }
  }

  logout(): boolean {
    console.log('logging out');
    localStorage.clear();
    return true;
  }

  isLoggedIn(): Boolean {
    return this.tokenData !== undefined;
  }

  getUsername(): string | undefined {
    return this.isLoggedIn() ? this.tokenData?.username : undefined;
  }
}

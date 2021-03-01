import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { ConfigurationService } from './configuration.service';
import { WSMessage, WSMessageType } from '../fetch/api';
import { Observable } from 'rxjs';
import { AuthService } from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  webSocket: WebSocketSubject<unknown> | undefined = undefined;
  chatObservable: Observable<any> | undefined = undefined;
  connected: boolean = false;

  constructor(private configService: ConfigurationService,
              private authService: AuthService) {}

  /**
   * IMPORTANT! THE USERNAME SHOULD BE VALIDATED BEFORE BEING PASSED
   * INTO THIS FUNCTION
   *
   * @param username - The username the player wants to use
   */
  connect(username: string): void {

    // We want to append the token to the url if the user is logged in.
    const url = this.authService.isLoggedIn() ?
      `${this.configService.webSocketUrl}/${username}/${this.authService.getToken()}` :
      `${this.configService.webSocketUrl}/${username}`

    this.webSocket = webSocket(url);

    // For handling websocket messages, checkout this guide:
    // https://rxjs-dev.firebaseapp.com/api/webSocket/webSocket
    this.chatObservable = this.webSocket.multiplex(
      () => (null),
      () => (null),
      rawMessage => {
        const msg = rawMessage as WSMessage;

        return msg.msgType === WSMessageType.BroadcastMessage ||
          msg.msgType === WSMessageType.InboxMessage;
      }
    );

    // Websocket closes if no message is received.
    // Send ping to server every 60 seconds to keep connection alive
    setInterval(() => {
      const pingData = {
        msgType: WSMessageType.PingMessage,
        data: {}
      } as WSMessage;

      this.sendMessage(pingData);
    }, 60 * 1000);
  }

  sendMessage(payload: any): void {
    this.webSocket ? this.webSocket.next(payload) : undefined
  }
}

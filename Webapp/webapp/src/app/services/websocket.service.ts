import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { ConfigurationService } from './configuration.service';
import { WebSocketMessage } from '../fetch/api';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  webSocket: WebSocketSubject<unknown>;

  constructor(private configService: ConfigurationService) {
    this.webSocket = webSocket(configService.wsUrl);

    // This is temporary... In the future, will most likely need
    // to multiplex. Refer to this guide on rxjs websocket:
    // https://rxjs-dev.firebaseapp.com/api/webSocket/webSocket
    this.webSocket.subscribe(
      msg => this.msgCallback(msg),
      err => console.log(err),
      () => console.log('connection closed.')
    );
  }

  /**
   * @remarks
   * Sends a websocket message to the server
   *
   * @param message The websocket message sent to server.
   */
  sendMessage(message: WebSocketMessage): void {
    this.webSocket.next(message);
  }

  /**
   * @remarks
   * This callback method is TEMPORARY and processes websocket messages
   *
   * @param msg message received from the websocket
   */
  private msgCallback(msg: unknown) {
    console.log('message received from websocket: ' + msg);
  }
}

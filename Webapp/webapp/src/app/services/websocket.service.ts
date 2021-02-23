import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { ConfigurationService } from './configuration.service';
import { WSMessage, WSMessageType } from '../fetch/api';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  webSocket: WebSocketSubject<unknown>;

  chatObservable: Observable<any>;

  constructor(private configService: ConfigurationService) {
    this.webSocket = webSocket(configService.wsUrl);

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

      this.webSocket.next(pingData);
    }, 60 * 1000);
  }

  sendMessage(payload: any): void {
    this.webSocket.next(payload);
  }
}

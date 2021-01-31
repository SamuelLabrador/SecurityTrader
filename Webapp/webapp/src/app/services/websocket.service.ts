import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { ConfigurationService } from './configuration.service';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  webSocket: WebSocketSubject<unknown>;

  constructor(private configService: ConfigurationService) {
    this.webSocket = webSocket(configService.wsUrl);

    // For handling websocket messages, checkout this guide:
    // https://rxjs-dev.firebaseapp.com/api/webSocket/webSocket
  }
}

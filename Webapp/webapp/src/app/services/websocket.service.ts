import { R3ExpressionFactoryMetadata } from '@angular/compiler/src/render3/r3_factory';
import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { ConfigurationService } from './configuration.service';
import { WebSocketMessage } from '../fetch/api';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  webSocket: WebSocketSubject<unknown>;

  constructor(private configService: ConfigurationService) { 
    this.webSocket = webSocket(configService.wsUrl);

    // TODO: Research and implement ws logic
  }

  createLobby() {
    const msg = {
      msgType: 'create', 
      data: {}
    } as WebSocketMessage;
  }
}

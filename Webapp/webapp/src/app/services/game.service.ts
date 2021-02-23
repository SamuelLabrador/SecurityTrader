import { Injectable } from '@angular/core';
import { WebSocketService } from './websocket.service';
import { ModelsRestWSCreateGame, ModelsRestWSJoinGame, WSMessage, WSMessageType } from '../fetch/api';

@Injectable({
  providedIn: 'root'
})
export class GameService {

  constructor(private wsService: WebSocketService) { }

  /**
   * Sends a CreateGame message to the websocket to create a game
   */
  requestCreateGame(): void {
    console.log('requesting for a game to be made');
    const payload = {
      msgType: WSMessageType.CreateGame,
      data: {}
    } as ModelsRestWSCreateGame;

    this.wsService.sendMessage(payload);
  }

  /**
   * Sends a JoinGame message to the websocket with specified game id
   * @param id The game id
   */
  requestJoinGame(id: string): void {
    const payload = {
      msgType: WSMessageType.JoinGame,
      data: {
        id
      } as ModelsRestWSJoinGame
    } as WSMessage;

    this.wsService.sendMessage(payload);
  }
}

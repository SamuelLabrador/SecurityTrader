import { Injectable } from '@angular/core';
import { WebSocketService } from './websocket.service';
import { ModelsRestWSCreateGame, ModelsRestWSJoinGame, WSMessage, WSMessageType } from '../fetch/api';
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class GameService {

  inGame: boolean = false;

  constructor(private wsService: WebSocketService) {
  }

  /**
   * Sends a CreateGame message to the websocket to create a game
   * @param username - The player's username
   */
  requestCreateGame(username: string) {
    this.wsService.connect(username);

    const createGameMessage = {
      msgType: WSMessageType.CreateGame,
      data: {}
    } as ModelsRestWSCreateGame;

    this.wsService.sendMessage(createGameMessage);
  }

  /**
   * Sends a JoinGame message to the websocket with specified game id
   * @param username - The player's username
   * @param id - The game id
   */
  requestJoinGame(username: string, id: string) {
    this.wsService.connect(username);
    this.inGame = true;

    const joinGameMessage = {
      msgType: WSMessageType.JoinGame,
      data: {
        id
      } as ModelsRestWSJoinGame
    } as WSMessage;

    this.wsService.sendMessage(joinGameMessage);
  }
}

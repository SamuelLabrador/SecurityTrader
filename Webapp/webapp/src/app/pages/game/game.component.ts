import { Component, OnInit } from '@angular/core';
import { MatGridList } from "@angular/material/grid-list";
import {WebSocketService} from "../../services/websocket.service";
import {ModelsRestWSCreateGame, WSMessageType} from "../../fetch/api";

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.sass', '../../app.component.sass']
})
export class GameComponent implements OnInit {

  constructor(private webSocketService: WebSocketService) { }

  ngOnInit(): void {
    const payload = {
      msgType: WSMessageType.CreateGame,
      data: {}
    } as ModelsRestWSCreateGame;

    this.webSocketService.webSocket.next(payload);
  }

}

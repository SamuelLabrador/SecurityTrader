import { Component, OnInit } from '@angular/core';
import { WebSocketService } from '../../services/websocket.service';

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.sass', '../../app.component.sass']
})
export class GameComponent implements OnInit {

  constructor(private webSocketService: WebSocketService) { }

  ngOnInit(): void { }
}

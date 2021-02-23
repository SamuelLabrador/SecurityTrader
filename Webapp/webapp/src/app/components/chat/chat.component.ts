import { Component, OnInit } from '@angular/core';
import { WebSocketService } from '../../services/websocket.service';
import { ModelsRestWSBroadcastMessage, ModelsRestWSInboxMessage, WSMessage, WSMessageType } from '../../fetch/api';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.sass']
})
export class ChatComponent implements OnInit {

  messageList: string[] = [];
  currentMessage = '';

  constructor(private ws: WebSocketService) {
    ws.chatObservable.subscribe( chatMessage => this.handleMessage(chatMessage.data) );
  }

  ngOnInit(): void { }

  clearValue(): void {
    this.currentMessage = '';
  }

  handleMessage(message: ModelsRestWSInboxMessage): void {
    this.messageList.push(message.message);
  }

  handleInput(event: KeyboardEvent): void {
    if (event.code === 'Enter' && this.currentMessage !== '') {
      this.sendMessage(this.currentMessage);
      this.currentMessage = '';
    }
  }

  sendMessage(message: string): void {
    const data = {
      msgType: WSMessageType.BroadcastMessage,
      data: {
        message
      } as ModelsRestWSBroadcastMessage
    } as WSMessage;

    this.ws.webSocket.next(data);
  }
}

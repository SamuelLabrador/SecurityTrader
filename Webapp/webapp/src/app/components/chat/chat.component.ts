import {
  AfterViewChecked,
  AfterViewInit,
  Component,
  ElementRef,
  OnInit,
  QueryList,
  ViewChild,
  ViewChildren
} from '@angular/core';
import { WebSocketService } from '../../services/websocket.service';
import { ModelsRestWSBroadcastMessage, ModelsRestWSInboxMessage, WSMessage, WSMessageType } from '../../fetch/api';

interface Message {
  time: String;
  username: String;
  message: String;
}

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.sass']
})
export class ChatComponent implements OnInit, AfterViewInit {

  // These private members are used for auto scrolling
  @ViewChild('chatWindow') chatWindow: ElementRef | undefined = undefined;
  @ViewChildren('messageList') messageListElement: QueryList<ModelsRestWSInboxMessage> | undefined = undefined;

  private scrollContainer: any;
  private messageList: ModelsRestWSInboxMessage[] = [];


  currentMessage = '';

  constructor(private ws: WebSocketService) {
    if (ws.chatObservable) {
      ws.chatObservable.subscribe( chatMessage => this.handleMessage(chatMessage.data) );
    } else {
      console.error("Chat is unable to connect to WebSocket");
    }
  }

  // This method is called when the component is instantiated
  ngOnInit(): void {
  }

  // This method is called after the elements are rendered
  ngAfterViewInit() {
    if (this.messageListElement) {
      this.messageListElement.changes.subscribe(_ => this.onItemElementsChanged());
    }

    if (this.chatWindow?.nativeElement) {
      this.scrollContainer = this.chatWindow.nativeElement;
    }
  }

  /**
   * Clears the current message from the UI
   */
  clearValue(): void {
    this.currentMessage = '';
  }

  handleMessage(message: ModelsRestWSInboxMessage): void {
    this.messageList.push(message);
  }

  /**
   * Handles input in the chatbox
   *
   * If the user hits 'enter,' it will send the message
   *
   * @param event - The KeyboardEvent
   */
  handleInput(event: KeyboardEvent): void {
    if (event.code === 'Enter' && this.currentMessage !== '') {
      this.sendMessage(this.currentMessage);
    }
  }

  /**
   * Sends a message asking the server to broadcast the message to other players
   *
   * @param message - The message the player wants to broadcast to other players
   */
  sendMessage(message: string): void {
    const data = {
      msgType: WSMessageType.BroadcastMessage,
      data: {
        message
      } as ModelsRestWSBroadcastMessage
    } as WSMessage;

    this.ws.sendMessage(data);
    this.currentMessage = '';
  }

  /**
   * Turns a timestamp into a time format: hour:minute:second
   * @param epoch
   */
  getTimeFormat(epoch: number): string {
    const date = new Date(epoch);

    // See DateTimeFormat constructor on developer.mozilla.org
    const formattingOptions = {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false
    };

    return date.toLocaleTimeString(undefined,  formattingOptions)
  }

  /**
   * If a new message is added to the list, this event triggers. Will scroll to the bottom if needed.
   */
  onItemElementsChanged(): void {
    this.scrollToBottom();
  }

  /**
   * Scrolls to the bottom of the chat box if the user is scrolled to the bottom of the chat
   * @private
   */
  private scrollToBottom(): void {
    if (this.isUserNearBottom()) {
      this.scrollContainer.scroll({
        top: this.scrollContainer.scrollHeight + 10,
        left: 0,
        behavior: 'smooth'
      });
    } else {
      console.log('not scrolling, not at bottom');
    }
  }

  /**
   * Checks if the user is scrolled to the bottom of the chat
   * @private
   */
  private isUserNearBottom(): boolean {
    const threshold = 150;
    const position = this.scrollContainer.scrollTop + this.scrollContainer.offsetHeight;
    const height = this.scrollContainer.scrollHeight;
    return position > height - threshold;
  }
}

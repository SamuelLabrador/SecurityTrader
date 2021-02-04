import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.sass']
})
export class ChatComponent implements OnInit {

  messageList: string[] = [];
  currentMessage = ''

  constructor() { }

  ngOnInit(): void {
  }

  clearValue() {
    this.currentMessage = '';
  }

  handleInput(event: any) {
    if (event.keyCode === 13 && this.currentMessage !== '') {
      console.log('Enter has been pressed');
      this.messageList.push(this.currentMessage);
      this.currentMessage = '';
    }
  }
}

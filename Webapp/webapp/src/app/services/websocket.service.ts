import { R3ExpressionFactoryMetadata } from '@angular/compiler/src/render3/r3_factory';
import { Injectable } from '@angular/core';
import * as Rx from "rxjs/Rx";

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private message: Rx.Subject<MessageEvent>;

  public connect(url): Rx.Subject<MessageEvent> {
    if(!this.subject) {
      this.subject = this.create(url);
    }
    return this.subject;
    
  }
  constructor() { }
}

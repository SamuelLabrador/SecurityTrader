import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ApiService } from './services/api.service';
import { ConfigurationService } from './services/configuration.service';
import { WebSocketService } from './services/websocket.service';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [
    ApiService,
    ConfigurationService,
    WebSocketService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

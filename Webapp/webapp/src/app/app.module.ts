import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ApiService } from './services/api.service';
import { Configuration } from 'security_trader_api';
import { ConfigurationService } from './services/configuration.service';
import { WebsocketService } from './services/websocket.service';

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
    WebsocketService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

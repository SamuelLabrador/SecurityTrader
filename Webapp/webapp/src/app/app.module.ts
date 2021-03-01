import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ApiService } from './services/api.service';
import { ConfigurationService } from './services/configuration.service';
import { WebSocketService } from './services/websocket.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatButtonModule } from "@angular/material/button";
import { MatGridListModule } from "@angular/material/grid-list";
import { GameComponent } from './pages/game/game.component';
import { LoginComponent } from './pages/login/login.component';
import { HomeComponent } from './pages/home/home.component';
import { HelpComponent } from './pages/help/help.component';
import { ChatComponent } from './components/chat/chat.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from "./services/auth.service";
import { PlayerListComponent } from './components/player-list/player-list.component';
import { GameSettingsComponent } from './components/game-settings/game-settings.component';

@NgModule({
  declarations: [
    AppComponent,
    GameComponent,
    LoginComponent,
    HomeComponent,
    HelpComponent,
    ChatComponent,
    PlayerListComponent,
    GameSettingsComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatGridListModule,
    MatIconModule,
    MatInputModule
  ],
  providers: [
    ApiService,
    ConfigurationService,
    WebSocketService,
    AuthService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

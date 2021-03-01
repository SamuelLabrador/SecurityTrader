import { Component, OnInit } from '@angular/core';
import { GameService } from '../../services/game.service';
import { FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.sass', '../../app.component.sass']
})
export class HomeComponent implements OnInit {
  gameID = new FormControl('');
  username = new FormControl('');

  constructor(private gameService: GameService,
              private router: Router,
              private authService: AuthService) { }

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.username.setValue(this.authService.getUsername());
    }
  }

  createGame(): void {
    this.gameService.requestCreateGame(this.username.value);
    this.router.navigate(['/game']);
  }

  joinGame(): void {
    this.gameService.requestJoinGame(this.username.value, this.gameID.value);
    this.router.navigate(['/game']);
  }

}

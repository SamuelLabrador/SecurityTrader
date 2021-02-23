import { Component, OnInit } from '@angular/core';
import { GameService } from '../../services/game.service';
import { FormControl } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.sass', '../../app.component.sass']
})
export class HomeComponent implements OnInit {

  gameID = new FormControl('');

  constructor(private gameService: GameService,
              private router: Router) { }

  ngOnInit(): void {
  }

  createGame(): void {
    this.gameService.requestCreateGame();
    this.router.navigate(['/game']);
  }

  joinGame(): void {
    this.gameService.requestJoinGame(this.gameID.value);
    this.router.navigate(['/game']);
  }

}

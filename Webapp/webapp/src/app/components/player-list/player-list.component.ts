import { Component, OnInit } from '@angular/core';
import { AuthService } from "../../services/auth.service";

@Component({
  selector: 'app-player-list',
  templateUrl: './player-list.component.html',
  styleUrls: ['./player-list.component.sass']
})
export class PlayerListComponent implements OnInit {

  playerList: String[] = [];

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    const maybeName = this.authService.getUsername();

    const name = maybeName ? maybeName : '';
    this.playerList.push(name);
  }

}

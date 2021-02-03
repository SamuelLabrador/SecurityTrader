import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { GameComponent } from "./component/pages/game/game.component";
import { LoginComponent } from "./component/pages/login/login.component";
import { HomeComponent } from "./component/pages/home/home.component";
import { HelpComponent } from "./component/pages/help/help.component";

const routes: Routes = [
  { path: '', component: HomeComponent},
  { path: 'login', component: LoginComponent},
  { path: 'game', component: GameComponent},
  { path: 'help', component: HelpComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

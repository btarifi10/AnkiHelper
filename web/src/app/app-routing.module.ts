import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {AddNewCardsComponent} from "./pages/add-new-cards/add-new-cards.component";
import {LandingPageComponent} from "./pages/landing-page/landing-page.component";
import {HelpComponent} from "./pages/help/help.component";
import {AboutComponent} from "./pages/about/about.component";

const routes: Routes = [
  { path: 'add-new-cards', component: AddNewCardsComponent },
  { path: 'landing', component: LandingPageComponent},
  { path: '', redirectTo: 'landing', pathMatch: "full"},
  { path: 'help', component: HelpComponent},
  { path: 'about', component: AboutComponent}
];


// @ts-ignore
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {AddNewCardsComponent} from "./pages/add-new-cards/add-new-cards.component";
import {LandingPageComponent} from "./pages/landing-page/landing-page.component";

const routes: Routes = [
  { path: 'add-new-cards', component: AddNewCardsComponent },
  { path: '', component: LandingPageComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { LandingPageComponent } from './pages/landing-page/landing-page.component';
import { AddNewCardsComponent } from './pages/add-new-cards/add-new-cards.component';
import { FormsModule } from "@angular/forms";
import { UploadFilesComponent } from './pages/upload-files/upload-files.component';
import { HttpClientModule} from "@angular/common/http";
import { HelpComponent } from './pages/help/help.component';
import { AboutComponent } from './pages/about/about.component';

@NgModule({
  declarations: [
    AppComponent,
    LandingPageComponent,
    AddNewCardsComponent,
    UploadFilesComponent,
    HelpComponent,
    AboutComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    FormsModule,
    HttpClientModule,

  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

import { Component, OnInit } from '@angular/core';
import {AnkiService} from "../../services/anki.service";

@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.css']
})
export class LandingPageComponent implements OnInit {
  active: boolean;

  constructor(private ankiService : AnkiService) { }

  ngOnInit(): void {
    this.checkConnection();
  }

  checkConnection() {
    this.ankiService.testConnection().subscribe(response => {
      console.log(response);
      if (!response || !response.result) {
        this.active = false;
      } else {
        this.active = true;
      }
    });
  }
}

import { Component, OnInit } from '@angular/core';
import {AnkiService} from "../../services/anki.service";
import {UploadFileService} from "../../services/upload-file.service";

@Component({
  selector: 'app-add-new-cards',
  templateUrl: './add-new-cards.component.html',
  styleUrls: ['./add-new-cards.component.css']
})
export class AddNewCardsComponent implements OnInit {

  public deckNames : string[] = [];
  public selectedDeck: string;
  public cardTitlesToReview: string[] = [];
  public responses : string[] = null;
  public stage : number = 1;

  constructor(private ankiService : AnkiService) { }

  ngOnInit(): void {
    this.ankiService.getDecks().subscribe( decks => {
      this.deckNames = decks;
    });
  }

  proceed() {
    this.ankiService.proceed().subscribe( cards => {
      this.cardTitlesToReview = cards;
      this.responses = null;
      this.stage = 3;
    });
  }

  confirm() {
    this.ankiService.confirm(this.selectedDeck).subscribe( responses => {
      this.responses = responses;
    });
  }


  deckSelection(deck: string) {
    this.selectedDeck = deck;
    this.stage = 2;
  }
}

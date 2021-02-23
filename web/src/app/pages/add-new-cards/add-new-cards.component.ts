import { Component, OnInit } from '@angular/core';
import {AnkiService} from "../../services/anki.service";
import {UploadFileService} from "../../services/upload-file.service";
import {AnkiResponseBody} from "../../models/AnkiResponseBody";
import {Note} from "../../models/Note";

@Component({
  selector: 'app-add-new-cards',
  templateUrl: './add-new-cards.component.html',
  styleUrls: ['./add-new-cards.component.css']
})
export class AddNewCardsComponent implements OnInit {

  public deckNames : string[] = [];
  public selectedDeck: string;
  public cardTitlesToReview: string[] = [];
  public notesToBeAdded : Note[] = [];
  public responses : AnkiResponseBody[] = null;
  public stage : number = 1;
  newDeckName: string;
  public readDeckFromNotes: boolean;
  public newDeck: boolean;

  constructor(private ankiService : AnkiService) { }

  ngOnInit(): void {
    this.ankiService.getDecks().subscribe( decks => {
      this.deckNames = decks;
    });
  }

  proceed() {
    this.ankiService.proceed(this.selectedDeck).subscribe( notes => {
      this.notesToBeAdded = notes;
      this.responses = null;
      this.stage = 3;

    });
  }

  confirm() {
    this.ankiService.confirm().subscribe( responses => {
      this.responses = responses;
    });
  }

  getCardResponse(note : Note) : string {
    let response: AnkiResponseBody = this.responses[this.notesToBeAdded.indexOf(note)];

    if (response.error) {
      return response.error;
    } else {
      return `Card created with ID ${response.result}.`;
    }

  }

  getNoteFirstLine(note: Note) : string {
    return note.front.split("</br>")[0];
  }

  createNewDeck() {
    this.selectedDeck = this.newDeckName
    this.newDeck = true;
    this.readDeckFromNotes = false;
  }

  useDeckFromNotes() {
    this.selectedDeck = "";
    this.readDeckFromNotes = true;
    this.newDeck = false;
  }

  deckSelected() {
    this.newDeck = false;
    this.readDeckFromNotes=false;
  }
}

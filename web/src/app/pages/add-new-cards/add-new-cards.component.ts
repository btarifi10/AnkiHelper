import { Component, OnInit } from '@angular/core';
import {AnkiService} from "../../services/anki.service";
import {forkJoin, Observable} from 'rxjs';
import {Note} from "../../models/Note";

@Component({
  selector: 'app-add-new-cards',
  templateUrl: './add-new-cards.component.html',
  styleUrls: ['./add-new-cards.component.css']
})
export class AddNewCardsComponent implements OnInit {

  public deckNames : string[] = [];
  public selectedDeck: string;
  public notesToBeAdded: Note[] = [];
  public addResponses: string[] = null;
  public stage : number = 1;
  public newDeckName: string;
  public readDeckFromNotes: boolean;
  public newDeck: boolean;

  constructor(private ankiService : AnkiService) { }

  ngOnInit(): void {
    this.ankiService.getDecks().subscribe( response => {
      this.deckNames = response.result;
    });
    this.ankiService.deleteFiles();
  }

  proceed() {
    this.ankiService.proceed(this.selectedDeck).subscribe( notes => {
      this.notesToBeAdded = notes;
      this.addResponses = null;
      this.stage = 3;
    });
  }

  postNotes(notesToAdd: Note[]) {
    const responses = [];

    let observables = notesToAdd.map(note => {
      return this.ankiService.postNoteToAnki(note)
    });

    forkJoin(observables).subscribe(resList => {
      for (const response of resList){
        if (!response) {
          responses.push(response);
        } else if (response.error) {
          responses.push(response.error);
        } else {
          responses.push(`Card created with ID ${response.result}.`);
        }
      }
    });
    return responses;
  }

  confirm() {
    this.addResponses = this.postNotes(this.notesToBeAdded);
    this.ankiService.deleteFiles();
  }

  getCardResponse(note : Note) : string {
    return this.addResponses[this.notesToBeAdded.indexOf(note)];
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

  deleteNote(note: Note) {
    const index = this.notesToBeAdded.indexOf(note);
    if (index > -1)
      this.notesToBeAdded.splice(index, 1);
    }

  clickDeckSelected() {
    this.stage = 2;
    if (this.newDeck) {
      this.ankiService.createNewAnkiDeck(this.newDeckName).subscribe(() => {
        this.ankiService.getDecks().subscribe(res => {
          this.deckNames = res.result;
        });
      })
    }
  }
}

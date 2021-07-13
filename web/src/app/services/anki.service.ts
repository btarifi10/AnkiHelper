import { Injectable } from '@angular/core';
import {HttpClient, HttpEvent, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AnkiResponseBody} from '../models/AnkiRequestResponse';
import {Note} from '../models/Note';

@Injectable({
  providedIn: 'root'
})
export class AnkiService {

  private baseUrl = '/api';
  private ankiServer = 'http://localhost:8765';

  constructor(private http: HttpClient) { }

  getDecks(): Observable<any> {
    const body = {
      action: 'deckNames',
      version: 6
    };

    return this.http.post(this.ankiServer, body);
  }

  proceed(selectedDeck: string): Observable<Note[]> {
    return this.http.get<Note[]>(`${this.baseUrl}/proceed?deckName=${selectedDeck}`);
  }

  createNewAnkiDeck(deckName: string): Observable<any> {
    const body = {
      action: 'createDeck',
      version: 6,
      params: {
        deck: deckName
      }
    };

    return this.http.post(this.ankiServer, body);
  }

  postNoteToAnki(note: Note): Observable<any> {
    let notefields = null;

    if (note.modelName === 'Cloze') {
      notefields = {
        Text: note.front,
        'Back Extra': note.back
      };
    } else {
      notefields = {
        Front: note.front,
        Back: note.back
      };
    }

    const body = {
      action: 'addNote',
      version: 6,
      params: {
        note: {
          deckName: note.deckName,
          modelName: note.modelName,
          fields: notefields
        }
      }
    };

    return this.http.post(this.ankiServer, body);
  }


  deleteFiles(): Observable<any> {
    return this.http.delete(`${this.baseUrl}/deleteFiles`);
  }

  testConnection(): Observable<any>{
    const body = {
      action: 'version',
      version: 6
    };
    return this.http.post(this.ankiServer, body);
  }
}

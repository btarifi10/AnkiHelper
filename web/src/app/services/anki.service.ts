import { Injectable } from '@angular/core';
import {HttpClient, HttpEvent, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
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

  confirm(): Observable<AnkiResponseBody[]> {
    return this.http.get<AnkiResponseBody[]>(`${this.baseUrl}/confirm`);
  }

  testConnection(): Observable<any>{
    const body = {
      action: 'version',
      version: 6
    };
    return this.http.post(this.ankiServer, body, {
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Access-Control-Allow-Origin': 'http://localhost:8765/'
      }
    });
  }
}

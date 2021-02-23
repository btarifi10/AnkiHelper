import { Injectable } from '@angular/core';
import {HttpClient, HttpEvent, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import {AnkiResponseBody} from "../models/AnkiResponseBody";
import {Note} from "../models/Note";

@Injectable({
  providedIn: 'root'
})
export class AnkiService {

  private baseUrl = '/api';

  constructor(private http: HttpClient) { }

  getDecks(): Observable<any> {
    return this.http.get(`${this.baseUrl}/decks`);
  }

  proceed(selectedDeck: string): Observable<Note[]> {
    return this.http.get<Note[]>(`${this.baseUrl}/proceed?deckName=${selectedDeck}`);

  }

  confirm(): Observable<AnkiResponseBody[]> {
    return this.http.get<AnkiResponseBody[]>(`${this.baseUrl}/confirm`);
  }

  testConnection() : Observable<boolean>{
    return this.http.get<boolean>(`${this.baseUrl}/test`);
  }
}

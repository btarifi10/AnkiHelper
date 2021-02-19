import { Injectable } from '@angular/core';
import {HttpClient, HttpEvent, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AnkiService {

  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  getDecks(): Observable<any> {
    return this.http.get(`${this.baseUrl}/decks`);
  }

  proceed(): Observable<any> {
    return this.http.get(`${this.baseUrl}/proceed`);
  }

  confirm(selectedDeck: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/confirm?deckName=${selectedDeck}`);
  }

}

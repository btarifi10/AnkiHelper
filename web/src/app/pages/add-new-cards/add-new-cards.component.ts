import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-add-new-cards',
  templateUrl: './add-new-cards.component.html',
  styleUrls: ['./add-new-cards.component.css']
})
export class AddNewCardsComponent implements OnInit {

  public deckNames : string[] = ["Pharmacology", "Pathology", "Pathophysiology", "Clinical Skills"];
  public selectedDeck: string;

  public stage : number = 1;

  constructor() { }
  ngOnInit(): void {
  }

}

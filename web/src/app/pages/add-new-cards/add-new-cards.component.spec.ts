import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddNewCardsComponent } from './add-new-cards.component';

describe('AddNewCardsComponent', () => {
  let component: AddNewCardsComponent;
  let fixture: ComponentFixture<AddNewCardsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddNewCardsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddNewCardsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

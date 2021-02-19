import { TestBed } from '@angular/core/testing';

import { AnkiService } from './anki.service';

describe('AnkiService', () => {
  let service: AnkiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AnkiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

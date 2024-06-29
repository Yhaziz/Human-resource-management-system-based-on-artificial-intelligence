import { TestBed } from '@angular/core/testing';

import { DgService } from './dg.service';

describe('DgService', () => {
  let service: DgService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DgService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

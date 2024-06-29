import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RrhLayoutComponent } from './rrh-layout.component';

describe('RrhLayoutComponent', () => {
  let component: RrhLayoutComponent;
  let fixture: ComponentFixture<RrhLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RrhLayoutComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RrhLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

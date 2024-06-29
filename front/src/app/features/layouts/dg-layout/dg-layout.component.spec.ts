import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DgLayoutComponent } from './dg-layout.component';

describe('DgLayoutComponent', () => {
  let component: DgLayoutComponent;
  let fixture: ComponentFixture<DgLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DgLayoutComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DgLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

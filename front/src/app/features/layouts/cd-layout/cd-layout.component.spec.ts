import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CdLayoutComponent } from './cd-layout.component';

describe('CdLayoutComponent', () => {
  let component: CdLayoutComponent;
  let fixture: ComponentFixture<CdLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CdLayoutComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CdLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

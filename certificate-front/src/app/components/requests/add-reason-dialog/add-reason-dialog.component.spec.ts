import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddReasonDialogComponent } from './add-reason-dialog.component';

describe('AddReasonDialogComponent', () => {
  let component: AddReasonDialogComponent;
  let fixture: ComponentFixture<AddReasonDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddReasonDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddReasonDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

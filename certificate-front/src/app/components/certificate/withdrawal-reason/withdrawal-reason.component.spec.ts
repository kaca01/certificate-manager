import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WithdrawalReasonComponent } from './withdrawal-reason.component';

describe('WithdrawalReasonComponent', () => {
  let component: WithdrawalReasonComponent;
  let fixture: ComponentFixture<WithdrawalReasonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WithdrawalReasonComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WithdrawalReasonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

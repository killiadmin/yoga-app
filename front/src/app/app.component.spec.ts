import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from './services/session.service';

import { AppComponent } from './app.component';

describe('AppComponent', () => {
  let component: AppComponent;
  let mockSessionService: Partial<SessionService>;
  let mockRouter: Partial<Router>;

  beforeEach(async () => {
    mockSessionService = {
      $isLogged: jest.fn().mockReturnValue(of(true)),
      logOut: jest.fn()
    };

    mockRouter = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatToolbarModule
      ],
      declarations: [
        AppComponent
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    const fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should return isLogged', () => {
    const result = component.$isLogged();

    expect(mockSessionService.$isLogged).toHaveBeenCalled();
    expect(result).toBeDefined();
  });

  it('should logout and navigate to home', () => {
    component.logout();

    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['']);
  });
});

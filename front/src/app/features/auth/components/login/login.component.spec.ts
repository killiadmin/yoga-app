import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of, throwError } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';
import { AuthService } from '../../services/auth.service';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';

import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jest.Mocked<AuthService>;
  let sessionService: jest.Mocked<SessionService>;
  let router: Router;

  const mockSessionInformation: SessionInformation = {
    token: 'mock-token',
    type: 'Bearer',
    id: 1,
    username: 'test@yoga.fr',
    firstName: 'Test',
    lastName: 'User',
    admin: false
  };

  beforeEach(async () => {
    const authServiceMock = {
      login: jest.fn()
    };

    const sessionServiceMock = {
      logIn: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: SessionService, useValue: sessionServiceMock }
      ],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;
    router = TestBed.inject(Router);

    jest.spyOn(router, 'navigate').mockResolvedValue(true);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit form successfully and navigate to sessions', () => {
    authService.login.mockReturnValue(of(mockSessionInformation));

    component.form.patchValue({
      email: 'test@yoga.fr',
      password: 'password123'
    });

    component.submit();

    expect(authService.login).toHaveBeenCalledWith({
      email: 'test@yoga.fr',
      password: 'password123'
    });
    expect(sessionService.logIn).toHaveBeenCalledWith(mockSessionInformation);
    expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
    expect(component.onError).toBe(false);
  });

  it('should handle login error', () => {
    authService.login.mockReturnValue(throwError(() => new Error('Login failed')));

    component.form.patchValue({
      email: 'test@yoga.fr',
      password: 'password321'
    });

    component.submit();

    expect(authService.login).toHaveBeenCalledWith({
      email: 'test@yoga.fr',
      password: 'password321'
    });
    expect(sessionService.logIn).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
    expect(component.onError).toBe(true);
  });
});

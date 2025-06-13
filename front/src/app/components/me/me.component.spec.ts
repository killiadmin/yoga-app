import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';
import { UserService } from 'src/app/services/user.service';
import { User } from '../../interfaces/user.interface';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';

import { MeComponent } from './me.component';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let mockUserService: Partial<UserService>;
  let mockRouter: Partial<Router>;
  let mockMatSnackBar: Partial<MatSnackBar>;

  const mockUser: User = {
    id: 1,
    email: 'test@yoga.fr',
    firstName: 'Test',
    lastName: 'User',
    admin: false,
    password: '',
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  const mockSessionInformation: SessionInformation = {
    token: 'mock-token',
    type: 'Bearer',
    id: 1,
    username: 'test@yoga.fr',
    firstName: 'Test',
    lastName: 'User',
    admin: true
  };

  const mockSessionService: Partial<SessionService> = {
    sessionInformation: mockSessionInformation,
    logOut: jest.fn(),
  };

  beforeEach(async () => {
    mockUserService = {
      getById: jest.fn().mockReturnValue(of(mockUser)),
      delete: jest.fn().mockReturnValue(of({}))
    };

    mockRouter = {
      navigate: jest.fn()
    };

    mockMatSnackBar = {
      open: jest.fn()
    };

    jest.spyOn(window.history, 'back').mockImplementation(() => {});

    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: mockUserService },
        { provide: Router, useValue: mockRouter },
        { provide: MatSnackBar, useValue: mockMatSnackBar }
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load user data on ngOnInit', () => {
    component.ngOnInit();

    expect(mockUserService.getById).toHaveBeenCalledWith('1');
    expect(component.user).toEqual(mockUser);
  });

  it('should go back when back is called', () => {
    component.back();

    expect(window.history.back).toHaveBeenCalled();
  });

  it('should delete user account successfully', () => {
    component.delete();

    expect(mockUserService.delete).toHaveBeenCalledWith('1');
    expect(mockMatSnackBar.open).toHaveBeenCalledWith(
      "Your account has been deleted !",
      'Close',
      { duration: 3000 }
    );
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });
});

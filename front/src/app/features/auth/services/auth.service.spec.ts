import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { LoginRequest } from '../interfaces/loginRequest.interface';
import { RegisterRequest } from '../interfaces/registerRequest.interface';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { expect } from '@jest/globals';

describe('AuthService', () => {
  let service: AuthService;
  let httpTestingController: HttpTestingController;

  const mockLoginRequest: LoginRequest = {
    email: 'test@yoga.fr',
    password: 'password123',
  };

  const mockRegisterRequest: RegisterRequest = {
    email: 'test@yoga.fr',
    firstName: 'Test',
    lastName: 'User',
    password: 'password123',
  };

  const mockSessionInformation: SessionInformation = {
    token: 'mock-token',
    type: 'Bearer',
    id: 1,
    username: 'test@yoga.fr',
    firstName: 'Test',
    lastName: 'User',
    admin: false,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });

    service = TestBed.inject(AuthService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should register user with POST request', () => {
    service.register(mockRegisterRequest).subscribe();

    const req = httpTestingController.expectOne('api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockRegisterRequest);

    req.flush(null);
  });

  it('should login user and return SessionInformation', () => {
    service.login(mockLoginRequest).subscribe(response => {
      expect(response).toEqual(mockSessionInformation);
    });

    const req = httpTestingController.expectOne('api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockLoginRequest);
    req.flush(mockSessionInformation);
  });
});

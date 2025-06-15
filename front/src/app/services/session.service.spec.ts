import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { SessionInformation } from '../interfaces/sessionInformation.interface';
import { SessionService } from './session.service';

describe('SessionService', () => {
  let service: SessionService;

  const mockSessionInformation: SessionInformation = {
    token: 'mock-token',
    type: 'Bearer',
    id: 1,
    username: 'TestUser',
    firstName: 'Test',
    lastName: 'User',
    admin: false
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return observable with initial state, login and logout successfully', (done) => {
    const expectedStates = [false, true, false];
    let stateIndex = 0;

    service.$isLogged().subscribe(isLogged => {
      expect(isLogged).toBe(expectedStates[stateIndex]);
      stateIndex++;

      if (stateIndex === expectedStates.length) {
        expect(service.isLogged).toBe(false);
        expect(service.sessionInformation).toBeUndefined();
        done();
      }
    });

    service.logIn(mockSessionInformation);
    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(mockSessionInformation);

    service.logOut();
  });
});

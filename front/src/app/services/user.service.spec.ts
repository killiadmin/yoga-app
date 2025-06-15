import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { User } from '../interfaces/user.interface';
import { UserService } from './user.service';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  const mockUser: User = {
    id: 1,
    email: 'test@yoga.fr',
    lastName: 'Test',
    firstName: 'User',
    admin: false,
    password: 'password123',
    createdAt: new Date('2025-06-01'),
    updatedAt: new Date('2025-06-02')
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get user by id and delete user successfully', () => {
    const userId = '1';

    service.getById(userId).subscribe(user => {
      expect(user).toEqual(mockUser);
    });

    const getReq = httpMock.expectOne(`api/user/${userId}`);
    expect(getReq.request.method).toBe('GET');
    getReq.flush(mockUser);

    service.delete(userId).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const deleteReq = httpMock.expectOne(`api/user/${userId}`);
    expect(deleteReq.request.method).toBe('DELETE');
    deleteReq.flush({});
  });
});

import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { Session } from '../interfaces/session.interface';
import { SessionApiService } from './session-api.service';

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  const mockSession: Session = {
    id: 1,
    name: 'Test Session',
    description: 'Test Description',
    date: new Date('2025-06-01'),
    teacher_id: 1,
    users: [1, 2],
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SessionApiService],
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('detail', () => {
    it('should retrieve session details successfully', () => {
      const sessionId = '1';

      service.detail(sessionId).subscribe(session => {
        expect(session).toEqual(mockSession);
      });

      const req = httpMock.expectOne(`api/session/${sessionId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockSession);
    });
  });

  describe('delete', () => {
    it('should delete session successfully', () => {
      const sessionId = '1';

      service.delete(sessionId).subscribe(response => {
        expect(response).toEqual({});
      });

      const req = httpMock.expectOne(`api/session/${sessionId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush({});
    });
  });

  describe('create', () => {
    it('should create session successfully', () => {
      const newSession = { ...mockSession };
      delete newSession.id;

      service.create(newSession).subscribe(session => {
        expect(session).toEqual(mockSession);
      });

      const req = httpMock.expectOne('api/session');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newSession);
      req.flush(mockSession);
    });
  });

  describe('update', () => {
    it('should update session successfully', () => {
      const sessionId = '1';
      const updatedSession = { ...mockSession, name: 'Updated Session' };

      service.update(sessionId, updatedSession).subscribe(session => {
        expect(session).toEqual(updatedSession);
      });

      const req = httpMock.expectOne(`api/session/${sessionId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updatedSession);
      req.flush(updatedSession);
    });
  });

  describe('participate', () => {
    it('should participate to session successfully', () => {
      const sessionId = '1';
      const userId = '123';

      service.participate(sessionId, userId).subscribe(response => {
        expect(response).toBeUndefined();
      });

      const req = httpMock.expectOne(`api/session/${sessionId}/participate/${userId}`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toBeNull();
      req.flush(null);
    });
  });

  describe('unParticipate', () => {
    it('should unparticipate from session successfully', () => {
      const sessionId = '1';
      const userId = '123';

      service.unParticipate(sessionId, userId).subscribe(response => {
        expect(response).toBeUndefined();
      });

      const req = httpMock.expectOne(`api/session/${sessionId}/participate/${userId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });
});

import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { Teacher } from '../interfaces/teacher.interface';
import { TeacherService } from './teacher.service';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  const mockTeacher: Teacher = {
    id: 1,
    firstName: 'User',
    lastName: 'First',
    createdAt: new Date('2025-06-01'),
    updatedAt: new Date('2025-06-02')
  };

  const mockTeachers: Teacher[] = [
    mockTeacher,
    {
      id: 2,
      firstName: 'User',
      lastName: 'Second',
      createdAt: new Date('2025-06-03'),
      updatedAt: new Date('2025-06-04')
    }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TeacherService]
    });
    service = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve all teachers and teacher detail successfully', () => {
    const teacherId = '1';

    service.all().subscribe(teachers => {
      expect(teachers).toEqual(mockTeachers);
      expect(teachers.length).toBe(2);
    });

    const allReq = httpMock.expectOne('api/teacher');
    expect(allReq.request.method).toBe('GET');
    allReq.flush(mockTeachers);

    service.detail(teacherId).subscribe(teacher => {
      expect(teacher).toEqual(mockTeacher);
    });

    const detailReq = httpMock.expectOne(`api/teacher/${teacherId}`);
    expect(detailReq.request.method).toBe('GET');
    detailReq.flush(mockTeacher);
  });
});

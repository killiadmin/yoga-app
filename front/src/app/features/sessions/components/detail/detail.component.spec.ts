import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from '../../../../services/session.service';
import { TeacherService } from '../../../../services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';
import { MatSnackBar } from '@angular/material/snack-bar';

import { DetailComponent } from './detail.component';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let matSnackBar: MatSnackBar;
  let router: Router;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1,
    }
  };

  const mockSessionApiService = {
    detail: jest.fn(),
    delete: jest.fn(),
    participate: jest.fn(),
    unParticipate: jest.fn(),
  };

  const mockTeacherService = {
    detail: jest.fn(),
  };

  const mockSession = {
    id: 1,
    name: 'Test Session',
    description: 'Test Description',
    date: new Date(),
    teacher_id: 1,
    users: [1, 2, 3],
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  const mockTeacher = {
    id: 1,
    lastName: 'Test',
    firstName: 'Teacher',
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([]),
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule,
      ],
      declarations: [DetailComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
      ],
    }).compileComponents();

    matSnackBar = TestBed.inject(MatSnackBar);
    router = TestBed.inject(Router);

    mockSessionApiService.detail.mockReturnValue(of(mockSession));
    mockTeacherService.detail.mockReturnValue(of(mockTeacher));

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize component properties on construction', () => {
    expect(component.isAdmin).toBe(true);
    expect(component.userId).toBe('1');
    expect(component.sessionId).toBeDefined();
  });

  it('should fetch session and teacher details on ngOnInit', () => {
    fixture.detectChanges();

    expect(mockSessionApiService.detail).toHaveBeenCalledWith(component.sessionId);
    expect(mockTeacherService.detail).toHaveBeenCalledWith(mockSession.teacher_id.toString());
    expect(component.session).toEqual(mockSession);
    expect(component.teacher).toEqual(mockTeacher);
    expect(component.isParticipate).toBe(true);
  });

  it('should call window.history.back() on back()', () => {
    const historySpy = jest.spyOn(window.history, 'back').mockImplementation();

    component.back();

    expect(historySpy).toHaveBeenCalled();
    historySpy.mockRestore();
  });

  it('should delete session and navigate to sessions', () => {
    const routerSpy = jest.spyOn(router, 'navigate').mockImplementation();
    const snackBarSpy = jest.spyOn(matSnackBar, 'open').mockImplementation();
    mockSessionApiService.delete.mockReturnValue(of({}));

    component.delete();

    expect(mockSessionApiService.delete).toHaveBeenCalledWith(component.sessionId);
    expect(snackBarSpy).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(routerSpy).toHaveBeenCalledWith(['sessions']);
  });

  it('should call participate API', () => {
    mockSessionApiService.participate.mockReturnValue(of({}));

    component.participate();

    expect(mockSessionApiService.participate).toHaveBeenCalledWith(component.sessionId, component.userId);
  });

  it('should call unParticipate API', () => {
    mockSessionApiService.unParticipate.mockReturnValue(of({}));

    component.unParticipate();

    expect(mockSessionApiService.unParticipate).toHaveBeenCalledWith(component.sessionId, component.userId);
  });
});

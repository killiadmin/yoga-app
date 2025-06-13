import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, ActivatedRouteSnapshot, ParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { Session } from '../../interfaces/session.interface';
import { SessionApiService } from '../../services/session-api.service';
import { FormComponent } from './form.component';

interface MockRouter {
  navigate: jest.Mock;
  url: string;
}

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let mockSessionService: Partial<SessionService>;
  let mockSessionApiService: Partial<SessionApiService>;
  let mockTeacherService: Partial<TeacherService>;
  let mockMatSnackBar: Partial<MatSnackBar>;
  let mockRouter: MockRouter;
  let mockActivatedRoute: Partial<ActivatedRoute>;

  const mockSessionInformation: SessionInformation = {
    token: 'mock-token',
    type: 'Bearer',
    id: 1,
    username: 'Test_User',
    firstName: 'Test',
    lastName: 'User',
    admin: true,
  };

  const mockSession: Session = {
    id: 1,
    name: 'Test Session',
    date: new Date('2025-06-01'),
    teacher_id: 1,
    description: 'Test description',
    users: [],
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  const mockTeachers = [
    { id: 1, firstName: 'Test', lastName: 'User' },
    { id: 2, firstName: 'Test2', lastName: 'User2' },
  ];

  const mockParamMap: ParamMap = {
    get: jest.fn().mockReturnValue('1'),
    has: jest.fn().mockReturnValue(true),
    getAll: jest.fn().mockReturnValue(['1']),
    keys: ['id'],
  };

  const mockActivatedRouteSnapshot: Partial<ActivatedRouteSnapshot> = {
    paramMap: mockParamMap,
    params: { id: '1' },
  };

  beforeEach(async () => {
    mockSessionService = {
      sessionInformation: mockSessionInformation,
    };

    mockSessionApiService = {
      detail: jest.fn(),
      create: jest.fn(),
      update: jest.fn(),
    };

    mockTeacherService = {
      all: jest.fn().mockReturnValue(of(mockTeachers)),
    };

    mockMatSnackBar = {
      open: jest.fn(),
    };

    mockRouter = {
      navigate: jest.fn(),
      url: '/sessions/create',
    };

    mockActivatedRoute = {
      snapshot: mockActivatedRouteSnapshot as ActivatedRouteSnapshot,
    };

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatButtonModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule,
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: MatSnackBar, useValue: mockMatSnackBar },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
      declarations: [FormComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form for create session', () => {
    mockRouter.url = '/sessions/create';

    component.ngOnInit();

    expect(component.onUpdate).toBeFalsy();
    expect(component.sessionForm).toBeDefined();
    expect(component.sessionForm?.get('name')?.value).toBe('');
  });

  it('should create new session successfully', () => {
    component.ngOnInit();
    component.onUpdate = false;

    const formValue = {
      name: 'New Session',
      date: '2025-06-01',
      teacher_id: 1,
      description: 'New description',
    };

    component.sessionForm?.patchValue(formValue);
    (mockSessionApiService.create as jest.Mock).mockReturnValue(of(mockSession));

    component.submit();

    expect(mockSessionApiService.create).toHaveBeenCalledWith(formValue);
    expect(mockMatSnackBar.open).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should have valid form when all fields are filled correctly', () => {
    component.ngOnInit();

    component.sessionForm?.patchValue({
      name: 'Valid Session',
      date: '2025-06-01',
      teacher_id: 1,
      description: 'Valid description',
    });

    expect(component.sessionForm?.valid).toBeTruthy();
  });

  it('should initialize teachers observable', () => {
    expect(component.teachers$).toBeDefined();

    component.teachers$.subscribe(teachers => {
      expect(teachers).toEqual(mockTeachers);
    });

    expect(mockTeacherService.all).toHaveBeenCalled();
  });
});

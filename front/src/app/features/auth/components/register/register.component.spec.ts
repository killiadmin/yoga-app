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
import { AuthService } from '../../services/auth.service';

import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: jest.Mocked<AuthService>;
  let router: Router;

  beforeEach(async () => {
    const authServiceMock = {
      register: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock }
      ],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        RouterTestingModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    router = TestBed.inject(Router);

    jest.spyOn(router, 'navigate').mockResolvedValue(true);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('submit', () => {
    it('should submit form successfully and navigate to login', () => {
      authService.register.mockReturnValue(of(void 0));

      const formValues = {
        email: 'test@yoga.fr',
        firstName: 'Killian',
        lastName: 'Filatre',
        password: 'password123'
      };

      component.form.patchValue(formValues);
      component.submit();

      expect(authService.register).toHaveBeenCalledWith(formValues);
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
      expect(component.onError).toBe(false);
    });

    it('should handle registration error', () => {
      authService.register.mockReturnValue(throwError(() => new Error('Registration failed')));

      const formValues = {
        email: 'test@yoga.fr',
        firstName: 'Killian',
        lastName: 'Filatre',
        password: 'password123'
      };

      component.form.patchValue(formValues);
      component.submit();

      expect(authService.register).toHaveBeenCalledWith(formValues);
      expect(router.navigate).not.toHaveBeenCalled();
      expect(component.onError).toBe(true);
    });
  });
});

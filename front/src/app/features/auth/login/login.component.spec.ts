import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../../../core/services/auth.service';
import { AuthResponse } from '../../../shared/models/user.model';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['login']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => expect(component).toBeTruthy());

  describe('form validation', () => {
    it('should be invalid when all fields are empty', () => {
      expect(component.form.invalid).toBeTrue();
    });

    it('should be invalid when identifier is missing', () => {
      component.form.patchValue({ identifier: '', password: 'secret123' });
      expect(component.form.invalid).toBeTrue();
    });

    it('should be invalid when password is missing', () => {
      component.form.patchValue({ identifier: 'john', password: '' });
      expect(component.form.invalid).toBeTrue();
    });

    it('should be valid when both identifier and password are filled', () => {
      component.form.patchValue({ identifier: 'john', password: 'secret123' });
      expect(component.form.valid).toBeTrue();
    });
  });

  describe('onSubmit', () => {
    it('should not call auth.login when the form is invalid', () => {
      component.onSubmit();
      expect(authServiceSpy.login).not.toHaveBeenCalled();
    });

    it('should call auth.login with form values on valid submission', () => {
      authServiceSpy.login.and.returnValue(of({} as AuthResponse));
      component.form.patchValue({ identifier: 'john', password: 'secret123' });

      component.onSubmit();

      expect(authServiceSpy.login).toHaveBeenCalledWith({ identifier: 'john', password: 'secret123' });
    });

    it('should navigate to /feed on successful login', fakeAsync(() => {
      authServiceSpy.login.and.returnValue(of({} as AuthResponse));
      const navigateSpy = spyOn(router, 'navigate');
      component.form.patchValue({ identifier: 'john', password: 'secret123' });

      component.onSubmit();
      tick();

      expect(navigateSpy).toHaveBeenCalledWith(['/feed']);
    }));

    it('should set the error signal on login failure', fakeAsync(() => {
      authServiceSpy.login.and.returnValue(throwError(() => new Error('Unauthorized')));
      component.form.patchValue({ identifier: 'john', password: 'wrongpass' });

      component.onSubmit();
      tick();

      expect(component.error()).toBe('Identifiants incorrects. Veuillez réessayer.');
    }));

    it('should reset loading to false on login failure', fakeAsync(() => {
      authServiceSpy.login.and.returnValue(throwError(() => new Error('Unauthorized')));
      component.form.patchValue({ identifier: 'john', password: 'wrongpass' });

      component.onSubmit();
      tick();

      expect(component.loading()).toBeFalse();
    }));

    it('should clear any previous error before a new submission', () => {
      authServiceSpy.login.and.returnValue(of({} as AuthResponse));
      component.error.set('Previous error');
      component.form.patchValue({ identifier: 'john', password: 'secret123' });

      component.onSubmit();

      expect(component.error()).toBe('');
    });
  });
});

import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../../core/services/auth.service';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['register']);

    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => expect(component).toBeTruthy());

  describe('form validation', () => {
    it('should be invalid when all fields are empty', () => {
      expect(component.form.invalid).toBeTrue();
    });

    it('should require a valid email address', () => {
      component.form.patchValue({ username: 'john', email: 'not-an-email', password: 'Test1234!' });
      expect(component.form.get('email')?.invalid).toBeTrue();
    });

    it('should accept a valid email address', () => {
      component.form.patchValue({ username: 'john', email: 'john@example.com', password: 'Test1234!' });
      expect(component.form.get('email')?.valid).toBeTrue();
    });

    it('should reject a password that does not meet security requirements', () => {
      component.form.patchValue({ username: 'john', email: 'john@example.com', password: 'short' });
      expect(component.form.get('password')?.invalid).toBeTrue();
    });

    it('should accept a password that meets all security requirements (8 chars, maj, min, chiffre, spécial)', () => {
      component.form.patchValue({ username: 'john', email: 'john@example.com', password: 'Test12!!' });
      expect(component.form.get('password')?.valid).toBeTrue();
    });

    it('should be valid when all fields pass validation', () => {
      component.form.patchValue({ username: 'john', email: 'john@example.com', password: 'Test1234!' });
      expect(component.form.valid).toBeTrue();
    });
  });

  describe('onSubmit', () => {
    it('should not call auth.register when the form is invalid', () => {
      component.onSubmit();
      expect(authServiceSpy.register).not.toHaveBeenCalled();
    });

    it('should call auth.register with form values on valid submission', () => {
      authServiceSpy.register.and.returnValue(of({} as any));
      component.form.patchValue({ username: 'john', email: 'john@example.com', password: 'Test1234!' });

      component.onSubmit();

      expect(authServiceSpy.register).toHaveBeenCalledWith({
        username: 'john',
        email: 'john@example.com',
        password: 'Test1234!'
      });
    });

    it('should navigate to /feed on successful registration', fakeAsync(() => {
      authServiceSpy.register.and.returnValue(of({} as any));
      const navigateSpy = spyOn(router, 'navigate');
      component.form.patchValue({ username: 'john', email: 'john@example.com', password: 'Test1234!' });

      component.onSubmit();
      tick();

      expect(navigateSpy).toHaveBeenCalledWith(['/feed']);
    }));

    it('should set the error signal on registration failure', fakeAsync(() => {
      authServiceSpy.register.and.returnValue(throwError(() => new Error('Conflict')));
      component.form.patchValue({ username: 'john', email: 'john@example.com', password: 'Test1234!' });

      component.onSubmit();
      tick();

      expect(component.error()).toBe('Une erreur est survenue. Veuillez réessayer.');
    }));

    it('should reset loading to false on registration failure', fakeAsync(() => {
      authServiceSpy.register.and.returnValue(throwError(() => new Error('Conflict')));
      component.form.patchValue({ username: 'john', email: 'john@example.com', password: 'Test1234!' });

      component.onSubmit();
      tick();

      expect(component.loading()).toBeFalse();
    }));

    it('should clear any previous error before a new submission', () => {
      authServiceSpy.register.and.returnValue(of({} as any));
      component.error.set('Previous error');
      component.form.patchValue({ username: 'john', email: 'john@example.com', password: 'Test1234!' });

      component.onSubmit();

      expect(component.error()).toBe('');
    });
  });
});

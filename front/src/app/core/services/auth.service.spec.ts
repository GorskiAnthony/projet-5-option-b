import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';
import { AuthResponse, User } from '../../shared/models/user.model';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let routerSpy: jasmine.SpyObj<Router>;

  const mockUser: User = { id: 1, username: 'john', email: 'john@example.com' };
  const mockAuthResponse: AuthResponse = { token: 'test-jwt-token', user: mockUser };

  beforeEach(() => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Router, useValue: routerSpy }
      ]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('login', () => {
    it('should POST credentials to /auth/login', () => {
      const credentials = { identifier: 'john', password: 'secret123' };
      service.login(credentials).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(credentials);
      req.flush(mockAuthResponse);
    });

    it('should store JWT token in localStorage on success', () => {
      service.login({ identifier: 'john', password: 'secret123' }).subscribe();
      httpMock.expectOne(`${environment.apiUrl}/auth/login`).flush(mockAuthResponse);

      expect(localStorage.getItem('mdd_token')).toBe('test-jwt-token');
    });

    it('should update currentUser signal on success', () => {
      service.login({ identifier: 'john', password: 'secret123' }).subscribe();
      httpMock.expectOne(`${environment.apiUrl}/auth/login`).flush(mockAuthResponse);

      expect(service.currentUser()).toEqual(mockUser);
    });
  });

  describe('register', () => {
    it('should POST registration data to /auth/register', () => {
      const data = { username: 'john', email: 'john@example.com', password: 'secret123' };
      service.register(data).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/register`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(data);
      req.flush(mockAuthResponse);
    });

    it('should store JWT token in localStorage on success', () => {
      service.register({ username: 'john', email: 'john@example.com', password: 'secret123' }).subscribe();
      httpMock.expectOne(`${environment.apiUrl}/auth/register`).flush(mockAuthResponse);

      expect(localStorage.getItem('mdd_token')).toBe('test-jwt-token');
    });

    it('should update currentUser signal on success', () => {
      service.register({ username: 'john', email: 'john@example.com', password: 'secret123' }).subscribe();
      httpMock.expectOne(`${environment.apiUrl}/auth/register`).flush(mockAuthResponse);

      expect(service.currentUser()).toEqual(mockUser);
    });
  });

  describe('logout', () => {
    beforeEach(() => {
      localStorage.setItem('mdd_token', 'existing-token');
      service.currentUser.set(mockUser);
    });

    it('should remove token from localStorage', () => {
      service.logout();
      expect(localStorage.getItem('mdd_token')).toBeNull();
    });

    it('should clear currentUser signal', () => {
      service.logout();
      expect(service.currentUser()).toBeNull();
    });

    it('should navigate to root', () => {
      service.logout();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
    });
  });

  describe('getToken', () => {
    it('should return token stored in localStorage', () => {
      localStorage.setItem('mdd_token', 'stored-token');
      expect(service.getToken()).toBe('stored-token');
    });

    it('should return null when localStorage has no token', () => {
      expect(service.getToken()).toBeNull();
    });
  });

  describe('isLoggedIn', () => {
    function buildMockJwt(exp: number): string {
      const payload = btoa(JSON.stringify({ exp }));
      return `header.${payload}.signature`;
    }

    it('should return true when token is present and not expired', () => {
      const token = buildMockJwt(Math.floor(Date.now() / 1000) + 3600);
      localStorage.setItem('mdd_token', token);
      expect(service.isLoggedIn()).toBeTrue();
    });

    it('should return false when token is expired', () => {
      const token = buildMockJwt(1); // exp en 1970 — toujours expiré
      localStorage.setItem('mdd_token', token);
      expect(service.isLoggedIn()).toBeFalse();
    });

    it('should return false when no token is in localStorage', () => {
      expect(service.isLoggedIn()).toBeFalse();
    });
  });
});

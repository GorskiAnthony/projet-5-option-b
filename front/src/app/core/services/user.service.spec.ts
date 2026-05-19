import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { UserService } from './user.service';
import { environment } from '../../../environments/environment';
import { User } from '../../shared/models/user.model';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  const mockUser: User = { id: 1, username: 'john', email: 'john@example.com' };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UserService, provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => expect(service).toBeTruthy());

  describe('getProfile', () => {
    it('should send GET request to /users/me', () => {
      service.getProfile().subscribe(user => expect(user).toEqual(mockUser));

      const req = httpMock.expectOne(`${environment.apiUrl}/users/me`);
      expect(req.request.method).toBe('GET');
      req.flush(mockUser);
    });
  });

  describe('updateProfile', () => {
    it('should send PUT request to /users/me with updated data', () => {
      const update = { username: 'john_updated', email: 'new@example.com' };
      service.updateProfile(update).subscribe(user => expect(user).toEqual(mockUser));

      const req = httpMock.expectOne(`${environment.apiUrl}/users/me`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(update);
      req.flush(mockUser);
    });

    it('should include password in body when provided', () => {
      const update = { username: 'john', email: 'john@example.com', password: 'newpass123' };
      service.updateProfile(update).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/users/me`);
      expect(req.request.body).toEqual(update);
      req.flush(mockUser);
    });
  });
});

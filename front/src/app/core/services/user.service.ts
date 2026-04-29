import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { User } from '../../shared/models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private http: HttpClient) {}

  getProfile() {
    return this.http.get<User>(`${environment.apiUrl}/users/me`);
  }

  updateProfile(body: Partial<User & { password: string }>) {
    return this.http.put<User>(`${environment.apiUrl}/users/me`, body);
  }
}

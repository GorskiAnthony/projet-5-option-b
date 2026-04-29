import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Topic } from '../../shared/models/topic.model';

@Injectable({ providedIn: 'root' })
export class TopicService {
  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<Topic[]>(`${environment.apiUrl}/topics`);
  }

  subscribe(id: number) {
    return this.http.post<void>(`${environment.apiUrl}/topics/${id}/subscribe`, {});
  }

  unsubscribe(id: number) {
    return this.http.delete<void>(`${environment.apiUrl}/topics/${id}/subscribe`);
  }
}

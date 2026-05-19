import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Post, CreatePostRequest } from '../../shared/models/post.model';

@Injectable({ providedIn: 'root' })
export class PostService {
  constructor(private http: HttpClient) {}

  getFeed() {
    return this.http.get<Post[]>(`${environment.apiUrl}/posts/feed`);
  }

  getById(id: number) {
    return this.http.get<Post>(`${environment.apiUrl}/posts/${id}`);
  }

  create(body: CreatePostRequest) {
    return this.http.post<Post>(`${environment.apiUrl}/posts`, body);
  }
}

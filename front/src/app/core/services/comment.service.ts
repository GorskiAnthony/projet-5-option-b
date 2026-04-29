import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Comment, CreateCommentRequest } from '../../shared/models/comment.model';

@Injectable({ providedIn: 'root' })
export class CommentService {
  constructor(private http: HttpClient) {}

  getByPost(postId: number) {
    return this.http.get<Comment[]>(`${environment.apiUrl}/posts/${postId}/comments`);
  }

  create(postId: number, body: CreateCommentRequest) {
    return this.http.post<Comment>(`${environment.apiUrl}/posts/${postId}/comments`, body);
  }
}

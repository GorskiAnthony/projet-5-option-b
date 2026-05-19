import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { CommentService } from './comment.service';
import { environment } from '../../../environments/environment';
import { Comment } from '../../shared/models/comment.model';

describe('CommentService', () => {
  let service: CommentService;
  let httpMock: HttpTestingController;

  const mockComment: Comment = {
    id: 1,
    content: 'Great post!',
    author: 'jane',
    createdAt: '2024-01-15T12:00:00',
    postId: 42
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CommentService, provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(CommentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => expect(service).toBeTruthy());

  describe('getByPost', () => {
    it('should send GET request to /posts/:postId/comments', () => {
      service.getByPost(42).subscribe(comments => expect(comments).toEqual([mockComment]));

      const req = httpMock.expectOne(`${environment.apiUrl}/posts/42/comments`);
      expect(req.request.method).toBe('GET');
      req.flush([mockComment]);
    });
  });

  describe('create', () => {
    it('should send POST request to /posts/:postId/comments with the given body', () => {
      const body = { content: 'Great post!' };
      service.create(42, body).subscribe(comment => expect(comment).toEqual(mockComment));

      const req = httpMock.expectOne(`${environment.apiUrl}/posts/42/comments`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(body);
      req.flush(mockComment);
    });
  });
});

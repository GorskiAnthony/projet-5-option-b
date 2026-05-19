import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { PostService } from './post.service';
import { environment } from '../../../environments/environment';
import { Post } from '../../shared/models/post.model';

describe('PostService', () => {
  let service: PostService;
  let httpMock: HttpTestingController;

  const mockPost: Post = {
    id: 1,
    title: 'Angular Tips',
    content: 'Some content',
    author: 'john',
    topicId: 1,
    topicName: 'Angular',
    createdAt: '2024-01-15T10:00:00'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PostService, provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => expect(service).toBeTruthy());

  describe('getFeed', () => {
    it('should send GET request to /posts/feed', () => {
      service.getFeed().subscribe(posts => expect(posts).toEqual([mockPost]));

      const req = httpMock.expectOne(`${environment.apiUrl}/posts/feed`);
      expect(req.request.method).toBe('GET');
      req.flush([mockPost]);
    });
  });

  describe('getById', () => {
    it('should send GET request to /posts/:id', () => {
      service.getById(1).subscribe(post => expect(post).toEqual(mockPost));

      const req = httpMock.expectOne(`${environment.apiUrl}/posts/1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPost);
    });
  });

  describe('create', () => {
    it('should send POST request to /posts with the given body', () => {
      const body = { topicId: 1, title: 'New Post', content: 'Content here' };
      service.create(body).subscribe(post => expect(post).toEqual(mockPost));

      const req = httpMock.expectOne(`${environment.apiUrl}/posts`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(body);
      req.flush(mockPost);
    });
  });
});

import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TopicService } from './topic.service';
import { environment } from '../../../environments/environment';
import { Topic } from '../../shared/models/topic.model';

describe('TopicService', () => {
  let service: TopicService;
  let httpMock: HttpTestingController;

  const mockTopics: Topic[] = [
    { id: 1, name: 'Angular', description: 'Angular framework', subscribed: true },
    { id: 2, name: 'Java', description: 'Java programming', subscribed: false }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TopicService, provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(TopicService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => expect(service).toBeTruthy());

  describe('getAll', () => {
    it('should send GET request to /topics', () => {
      service.getAll().subscribe(topics => expect(topics).toEqual(mockTopics));

      const req = httpMock.expectOne(`${environment.apiUrl}/topics`);
      expect(req.request.method).toBe('GET');
      req.flush(mockTopics);
    });
  });

  describe('subscribe', () => {
    it('should send POST request to /topics/:id/subscribe', () => {
      service.subscribe(1).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/topics/1/subscribe`);
      expect(req.request.method).toBe('POST');
      req.flush(null);
    });
  });

  describe('unsubscribe', () => {
    it('should send DELETE request to /topics/:id/subscribe', () => {
      service.unsubscribe(1).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/topics/1/subscribe`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });
});

import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Component } from '@angular/core';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { CreatePostComponent } from './create-post.component';
import { PostService } from '../../../core/services/post.service';
import { TopicService } from '../../../core/services/topic.service';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { Topic } from '../../../shared/models/topic.model';
import { Post } from '../../../shared/models/post.model';

@Component({ selector: 'app-navbar', template: '', standalone: true })
class NavbarStubComponent {}

describe('CreatePostComponent', () => {
  let component: CreatePostComponent;
  let fixture: ComponentFixture<CreatePostComponent>;
  let postServiceSpy: jasmine.SpyObj<PostService>;
  let topicServiceSpy: jasmine.SpyObj<TopicService>;
  let router: Router;

  const mockTopics: Topic[] = [
    { id: 1, name: 'Angular', description: 'Angular framework' },
    { id: 2, name: 'Java', description: 'Java programming' }
  ];

  const mockCreatedPost: Post = {
    id: 99, title: 'My Post', content: 'Content', author: 'john',
    topicId: 1, topicName: 'Angular', createdAt: '2024-01-15'
  };

  beforeEach(async () => {
    postServiceSpy = jasmine.createSpyObj('PostService', ['create']);
    topicServiceSpy = jasmine.createSpyObj('TopicService', ['getAll']);
    topicServiceSpy.getAll.and.returnValue(of(mockTopics));

    await TestBed.configureTestingModule({
      imports: [CreatePostComponent],
      providers: [
        { provide: PostService, useValue: postServiceSpy },
        { provide: TopicService, useValue: topicServiceSpy },
        provideRouter([])
      ]
    })
    .overrideComponent(CreatePostComponent, {
      remove: { imports: [NavbarComponent] },
      add: { imports: [NavbarStubComponent] }
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreatePostComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => expect(component).toBeTruthy());

  it('should load topics on init', () => {
    expect(topicServiceSpy.getAll).toHaveBeenCalledTimes(1);
    expect(component.topics()).toEqual(mockTopics);
  });

  describe('form validation', () => {
    it('should be invalid when all fields are empty', () => {
      expect(component.form.invalid).toBeTrue();
    });

    it('should be invalid when topicId is missing', () => {
      component.form.patchValue({ topicId: '', title: 'Title', content: 'Content' });
      expect(component.form.invalid).toBeTrue();
    });

    it('should be invalid when title is missing', () => {
      component.form.patchValue({ topicId: '1', title: '', content: 'Content' });
      expect(component.form.invalid).toBeTrue();
    });

    it('should be invalid when content is missing', () => {
      component.form.patchValue({ topicId: '1', title: 'Title', content: '' });
      expect(component.form.invalid).toBeTrue();
    });

    it('should be valid when all required fields are filled', () => {
      component.form.patchValue({ topicId: '1', title: 'My Title', content: 'Some content' });
      expect(component.form.valid).toBeTrue();
    });
  });

  describe('onSubmit', () => {
    it('should not call PostService.create when the form is invalid', () => {
      component.onSubmit();
      expect(postServiceSpy.create).not.toHaveBeenCalled();
    });

    it('should call PostService.create with typed topicId and form values', () => {
      postServiceSpy.create.and.returnValue(of(mockCreatedPost));
      component.form.patchValue({ topicId: '1', title: 'My Title', content: 'Content here' });

      component.onSubmit();

      expect(postServiceSpy.create).toHaveBeenCalledWith({ topicId: 1, title: 'My Title', content: 'Content here' });
    });

    it('should navigate to /posts/:id after successful creation', fakeAsync(() => {
      postServiceSpy.create.and.returnValue(of(mockCreatedPost));
      const navigateSpy = spyOn(router, 'navigate');
      component.form.patchValue({ topicId: '1', title: 'My Title', content: 'Content' });

      component.onSubmit();
      tick();

      expect(navigateSpy).toHaveBeenCalledWith(['/posts', mockCreatedPost.id]);
    }));

    it('should set the error signal on creation failure', () => {
      postServiceSpy.create.and.returnValue(throwError(() => new Error('Server error')));
      component.form.patchValue({ topicId: '1', title: 'My Title', content: 'Content' });

      component.onSubmit();

      expect(component.error()).toBe('Erreur lors de la création.');
    });

    it('should reset loading to false on creation failure', () => {
      postServiceSpy.create.and.returnValue(throwError(() => new Error('Server error')));
      component.form.patchValue({ topicId: '1', title: 'My Title', content: 'Content' });

      component.onSubmit();

      expect(component.loading()).toBeFalse();
    });
  });
});

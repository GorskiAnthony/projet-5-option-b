import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { PostDetailComponent } from './post-detail.component';
import { PostService } from '../../../core/services/post.service';
import { CommentService } from '../../../core/services/comment.service';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { Post } from '../../../shared/models/post.model';
import { Comment } from '../../../shared/models/comment.model';

@Component({ selector: 'app-navbar', template: '', standalone: true })
class NavbarStubComponent {}

describe('PostDetailComponent', () => {
  let component: PostDetailComponent;
  let fixture: ComponentFixture<PostDetailComponent>;
  let postServiceSpy: jasmine.SpyObj<PostService>;
  let commentServiceSpy: jasmine.SpyObj<CommentService>;

  const mockPost: Post = {
    id: 42, title: 'Test Post', content: 'Detailed content',
    author: 'john', topicId: 1, topicName: 'Angular', createdAt: '2024-01-15T10:00:00'
  };

  const mockComments: Comment[] = [
    { id: 1, content: 'First comment', author: 'alice', createdAt: '2024-01-15T11:00:00', postId: 42 }
  ];

  const activatedRouteStub = {
    snapshot: { paramMap: { get: (_: string) => '42' } }
  };

  beforeEach(async () => {
    postServiceSpy = jasmine.createSpyObj('PostService', ['getById']);
    commentServiceSpy = jasmine.createSpyObj('CommentService', ['getByPost', 'create']);
    postServiceSpy.getById.and.returnValue(of(mockPost));
    commentServiceSpy.getByPost.and.returnValue(of(mockComments));

    await TestBed.configureTestingModule({
      imports: [PostDetailComponent],
      providers: [
        { provide: PostService, useValue: postServiceSpy },
        { provide: CommentService, useValue: commentServiceSpy },
        provideRouter([]),
        { provide: ActivatedRoute, useValue: activatedRouteStub }
      ]
    })
    .overrideComponent(PostDetailComponent, {
      remove: { imports: [NavbarComponent] },
      add: { imports: [NavbarStubComponent] }
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => expect(component).toBeTruthy());

  it('should load the post by ID extracted from route params', () => {
    expect(postServiceSpy.getById).toHaveBeenCalledWith(42);
    expect(component.post()).toEqual(mockPost);
  });

  it('should load comments for the post on init', () => {
    expect(commentServiceSpy.getByPost).toHaveBeenCalledWith(42);
    expect(component.comments()).toEqual(mockComments);
  });

  it('should set loading to false after the post is loaded', () => {
    expect(component.loading()).toBeFalse();
  });

  describe('sendComment', () => {
    it('should not call CommentService.create when newComment is empty', () => {
      component.newComment = '';
      component.sendComment();
      expect(commentServiceSpy.create).not.toHaveBeenCalled();
    });

    it('should not call CommentService.create when newComment is only whitespace', () => {
      component.newComment = '   ';
      component.sendComment();
      expect(commentServiceSpy.create).not.toHaveBeenCalled();
    });

    it('should call CommentService.create with the post ID and comment content', () => {
      const newComment: Comment = { id: 2, content: 'New comment', author: 'bob', createdAt: '2024-01-15T12:00:00', postId: 42 };
      commentServiceSpy.create.and.returnValue(of(newComment));
      component.newComment = 'New comment';

      component.sendComment();

      expect(commentServiceSpy.create).toHaveBeenCalledWith(42, { content: 'New comment' });
    });

    it('should append the new comment to the comments list', () => {
      const newComment: Comment = { id: 2, content: 'New comment', author: 'bob', createdAt: '2024-01-15T12:00:00', postId: 42 };
      commentServiceSpy.create.and.returnValue(of(newComment));
      component.newComment = 'New comment';

      component.sendComment();

      expect(component.comments().length).toBe(2);
      expect(component.comments()[1]).toEqual(newComment);
    });

    it('should clear newComment after sending', () => {
      const newComment: Comment = { id: 2, content: 'New comment', author: 'bob', createdAt: '2024-01-15T12:00:00', postId: 42 };
      commentServiceSpy.create.and.returnValue(of(newComment));
      component.newComment = 'New comment';

      component.sendComment();

      expect(component.newComment).toBe('');
    });
  });
});

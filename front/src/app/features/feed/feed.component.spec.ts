import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, Input } from '@angular/core';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { FeedComponent } from './feed.component';
import { PostService } from '../../core/services/post.service';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { PostCardComponent } from '../../shared/components/post-card/post-card.component';
import { Post } from '../../shared/models/post.model';

@Component({ selector: 'app-navbar', template: '', standalone: true })
class NavbarStubComponent {}

@Component({ selector: 'app-post-card', template: '', standalone: true })
class PostCardStubComponent { @Input() post: any; }

describe('FeedComponent', () => {
  let component: FeedComponent;
  let fixture: ComponentFixture<FeedComponent>;
  let postServiceSpy: jasmine.SpyObj<PostService>;

  const mockPosts: Post[] = [
    { id: 1, title: 'Old Post', content: 'Content A', author: 'alice', topicId: 1, topicName: 'Angular', createdAt: '2024-01-01T10:00:00' },
    { id: 2, title: 'New Post', content: 'Content B', author: 'bob', topicId: 2, topicName: 'Java', createdAt: '2024-01-02T10:00:00' }
  ];

  beforeEach(async () => {
    postServiceSpy = jasmine.createSpyObj('PostService', ['getFeed']);
    postServiceSpy.getFeed.and.returnValue(of(mockPosts));

    await TestBed.configureTestingModule({
      imports: [FeedComponent],
      providers: [
        { provide: PostService, useValue: postServiceSpy },
        provideRouter([])
      ]
    })
    .overrideComponent(FeedComponent, {
      remove: { imports: [NavbarComponent, PostCardComponent] },
      add: { imports: [NavbarStubComponent, PostCardStubComponent] }
    })
    .compileComponents();

    fixture = TestBed.createComponent(FeedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => expect(component).toBeTruthy());

  it('should call PostService.getFeed on init', () => {
    expect(postServiceSpy.getFeed).toHaveBeenCalledTimes(1);
  });

  it('should populate the posts signal with fetched data', () => {
    expect(component.posts()).toEqual(mockPosts);
  });

  it('should set loading to false after data is fetched', () => {
    expect(component.loading()).toBeFalse();
  });

  it('should set loading to false when fetch fails', () => {
    postServiceSpy.getFeed.and.returnValue(throwError(() => new Error('Network error')));
    const newFixture = TestBed.createComponent(FeedComponent);
    newFixture.detectChanges();
    expect(newFixture.componentInstance.loading()).toBeFalse();
  });

  describe('toggleSort', () => {
    it('should toggle the sortDesc signal', () => {
      const initial = component.sortDesc();
      component.toggleSort();
      expect(component.sortDesc()).toBe(!initial);
    });

    it('should sort posts ascending (oldest first) on first toggle — sortDesc starts true', () => {
      component.posts.set([...mockPosts]);
      component.sortDesc.set(true);
      component.toggleSort();
      const sorted = component.posts();
      expect(new Date(sorted[0].createdAt).getTime())
        .toBeLessThan(new Date(sorted[1].createdAt).getTime());
    });

    it('should sort posts descending (newest first) when sortDesc becomes true', () => {
      component.posts.set([...mockPosts]);
      component.sortDesc.set(false);
      component.toggleSort();
      const sorted = component.posts();
      expect(new Date(sorted[0].createdAt).getTime())
        .toBeGreaterThan(new Date(sorted[1].createdAt).getTime());
    });
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { PostCardComponent } from './post-card.component';
import { Post } from '../../models/post.model';

describe('PostCardComponent', () => {
  let component: PostCardComponent;
  let fixture: ComponentFixture<PostCardComponent>;

  const mockPost: Post = {
    id: 1,
    title: 'Test Post Title',
    content: 'This is the full content of the test post.',
    author: 'john',
    topicId: 1,
    topicName: 'Angular',
    createdAt: '2024-01-15T10:00:00'
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostCardComponent],
      providers: [provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(PostCardComponent);
    component = fixture.componentInstance;
    component.post = mockPost;
    fixture.detectChanges();
  });

  it('should create', () => expect(component).toBeTruthy());

  it('should accept a Post object as required input', () => {
    expect(component.post).toEqual(mockPost);
  });

  it('should render the post title', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain(mockPost.title);
  });

  it('should render the post author', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain(mockPost.author);
  });

  it('should render the post content', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain(mockPost.content);
  });

  it('should render a link pointing to the post detail page', () => {
    const link = fixture.nativeElement.querySelector('a');
    expect(link).not.toBeNull();
    expect(link.getAttribute('href')).toBe(`/posts/${mockPost.id}`);
  });

  it('should render the creation date (first 10 characters)', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('2024-01-15');
  });
});

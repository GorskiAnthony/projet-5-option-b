import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { PostCardComponent } from '../../shared/components/post-card/post-card.component';
import { PostService } from '../../core/services/post.service';
import { Post } from '../../shared/models/post.model';

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [RouterLink, NavbarComponent, PostCardComponent],
  templateUrl: './feed.component.html',
  styleUrl: './feed.component.css'
})
export class FeedComponent implements OnInit {
  private postService = inject(PostService);

  posts = signal<Post[]>([]);
  loading = signal(true);
  sortDesc = signal(true);

  ngOnInit() {
    this.postService.getFeed().subscribe({
      next: posts => {
        this.posts.set(posts);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  toggleSort() {
    this.sortDesc.update(v => !v);
    this.posts.update(posts =>
      [...posts].sort((a, b) => {
        const diff = new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();
        return this.sortDesc() ? -diff : diff;
      })
    );
  }
}

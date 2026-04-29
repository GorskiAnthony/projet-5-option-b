import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SlicePipe } from '@angular/common';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { PostService } from '../../../core/services/post.service';
import { CommentService } from '../../../core/services/comment.service';
import { Post } from '../../../shared/models/post.model';
import { Comment } from '../../../shared/models/comment.model';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [NavbarComponent, RouterLink, FormsModule, SlicePipe],
  templateUrl: './post-detail.component.html',
  styleUrl: './post-detail.component.css'
})
export class PostDetailComponent implements OnInit {
  post = signal<Post | null>(null);
  comments = signal<Comment[]>([]);
  newComment = '';
  loading = signal(true);

  constructor(
    private route: ActivatedRoute,
    private postService: PostService,
    private commentService: CommentService
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.postService.getById(id).subscribe({
      next: post => {
        this.post.set(post);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
    this.commentService.getByPost(id).subscribe({
      next: comments => this.comments.set(comments)
    });
  }

  sendComment() {
    const postId = this.post()?.id;
    if (!postId || !this.newComment.trim()) return;
    this.commentService.create(postId, { content: this.newComment }).subscribe({
      next: comment => {
        this.comments.update(list => [...list, comment]);
        this.newComment = '';
      }
    });
  }
}

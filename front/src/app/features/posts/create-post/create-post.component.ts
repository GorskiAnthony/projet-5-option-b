import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { PostService } from '../../../core/services/post.service';
import { TopicService } from '../../../core/services/topic.service';
import { Topic } from '../../../shared/models/topic.model';

@Component({
  selector: 'app-create-post',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, NavbarComponent],
  templateUrl: './create-post.component.html',
  styleUrl: './create-post.component.css',
})
export class CreatePostComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly postService = inject(PostService);
  private readonly topicService = inject(TopicService);
  private readonly router = inject(Router);

  topics = signal<Topic[]>([]);
  loading = signal(false);
  error = signal('');

  form = this.fb.group({
    topicId: ['', Validators.required],
    title: ['', Validators.required],
    content: ['', Validators.required],
  });

  ngOnInit() {
    this.topicService.getAll().subscribe({
      next: (topics) => this.topics.set(topics),
    });
  }

  onSubmit() {
    if (this.form.invalid) return;
    this.loading.set(true);
    const { topicId, title, content } = this.form.value;
    this.postService
      .create({ topicId: Number(topicId), title: title!, content: content! })
      .subscribe({
        next: (post) => this.router.navigate(['/posts', post.id]),
        error: () => {
          this.error.set('Erreur lors de la création.');
          this.loading.set(false);
        },
      });
  }
}

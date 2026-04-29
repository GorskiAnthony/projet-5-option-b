import { Component, OnInit, signal } from '@angular/core';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { TopicService } from '../../core/services/topic.service';
import { Topic } from '../../shared/models/topic.model';

@Component({
  selector: 'app-topics',
  standalone: true,
  imports: [NavbarComponent],
  templateUrl: './topics.component.html',
  styleUrl: './topics.component.css'
})
export class TopicsComponent implements OnInit {
  topics = signal<Topic[]>([]);
  loading = signal(true);

  constructor(private topicService: TopicService) {}

  ngOnInit() {
    this.topicService.getAll().subscribe({
      next: topics => { this.topics.set(topics); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  toggleSubscription(topic: Topic) {
    const action$ = topic.subscribed
      ? this.topicService.unsubscribe(topic.id)
      : this.topicService.subscribe(topic.id);

    action$.subscribe(() =>
      this.topics.update(list =>
        list.map(t => t.id === topic.id ? { ...t, subscribed: !t.subscribed } : t)
      )
    );
  }
}

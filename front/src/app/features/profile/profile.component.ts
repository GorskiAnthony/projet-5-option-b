import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { UserService } from '../../core/services/user.service';
import { TopicService } from '../../core/services/topic.service';
import { Topic } from '../../shared/models/topic.model';
import { UpdateProfileRequest } from '../../shared/models/user.model';
import { passwordStrengthValidator } from '../../shared/validators/password.validator';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [ReactiveFormsModule, NavbarComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private topicService = inject(TopicService);

  subscriptions = signal<Topic[]>([]);
  saved = signal(false);
  error = signal('');

  form = this.fb.group({
    username: ['', [Validators.minLength(3), Validators.maxLength(30)]],
    email: ['', [Validators.email]],
    password: ['', [passwordStrengthValidator]]
  });

  ngOnInit() {
    this.userService.getProfile().subscribe({
      next: user => this.form.patchValue({ username: user.username, email: user.email })
    });
    this.topicService.getAll().subscribe({
      next: topics => this.subscriptions.set(topics.filter(t => t.subscribed))
    });
  }

  onSave() {
    this.error.set('');
    const { username, email, password } = this.form.value;
    const body: UpdateProfileRequest = { username: username!, email: email! };
    if (password) body.password = password;

    this.userService.updateProfile(body).subscribe({
      next: () => { this.saved.set(true); setTimeout(() => this.saved.set(false), 2000); },
      error: () => this.error.set('Erreur lors de la sauvegarde.')
    });
  }

  unsubscribe(topic: Topic) {
    this.topicService.unsubscribe(topic.id).subscribe(() =>
      this.subscriptions.update(list => list.filter(t => t.id !== topic.id))
    );
  }
}

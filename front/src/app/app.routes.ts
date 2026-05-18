import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/auth/landing/landing.component').then(
        (m) => m.LandingComponent,
      ),
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then(
        (m) => m.LoginComponent,
      ),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component').then(
        (m) => m.RegisterComponent,
      ),
  },
  {
    path: 'feed',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/feed/feed.component').then((m) => m.FeedComponent),
  },
  {
    path: 'topics',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/topics/topics.component').then(
        (m) => m.TopicsComponent,
      ),
  },
  {
    path: 'posts/create',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/posts/create-post/create-post.component').then(
        (m) => m.CreatePostComponent,
      ),
  },
  {
    path: 'posts/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/posts/post-detail/post-detail.component').then(
        (m) => m.PostDetailComponent,
      ),
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/profile/profile.component').then(
        (m) => m.ProfileComponent,
      ),
  },
  { path: '**', redirectTo: '' },
];

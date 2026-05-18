import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Component } from '@angular/core';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ProfileComponent } from './profile.component';
import { UserService } from '../../core/services/user.service';
import { TopicService } from '../../core/services/topic.service';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { UpdateProfileRequest, User } from '../../shared/models/user.model';
import { Topic } from '../../shared/models/topic.model';

@Component({ selector: 'app-navbar', template: '', standalone: true })
class NavbarStubComponent {}

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let topicServiceSpy: jasmine.SpyObj<TopicService>;

  const mockUser: User = { id: 1, username: 'john', email: 'john@example.com' };

  const mockTopics: Topic[] = [
    { id: 1, name: 'Angular', description: 'Angular framework', subscribed: true },
    { id: 2, name: 'Java', description: 'Java programming', subscribed: false }
  ];

  beforeEach(async () => {
    userServiceSpy = jasmine.createSpyObj('UserService', ['getProfile', 'updateProfile']);
    topicServiceSpy = jasmine.createSpyObj('TopicService', ['getAll', 'unsubscribe']);
    userServiceSpy.getProfile.and.returnValue(of(mockUser));
    topicServiceSpy.getAll.and.returnValue(of(mockTopics));

    await TestBed.configureTestingModule({
      imports: [ProfileComponent],
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: TopicService, useValue: topicServiceSpy },
        provideRouter([])
      ]
    })
    .overrideComponent(ProfileComponent, {
      remove: { imports: [NavbarComponent] },
      add: { imports: [NavbarStubComponent] }
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => expect(component).toBeTruthy());

  describe('ngOnInit', () => {
    it('should load user profile and patch form with username and email', () => {
      expect(userServiceSpy.getProfile).toHaveBeenCalledTimes(1);
      expect(component.form.value.username).toBe('john');
      expect(component.form.value.email).toBe('john@example.com');
    });

    it('should load topics and filter to subscribed ones only', () => {
      expect(topicServiceSpy.getAll).toHaveBeenCalledTimes(1);
      const subscriptions = component.subscriptions();
      expect(subscriptions.length).toBe(1);
      expect(subscriptions[0].id).toBe(1);
      expect(subscriptions[0].name).toBe('Angular');
    });
  });

  describe('onSave', () => {
    it('should call UserService.updateProfile with username and email', () => {
      userServiceSpy.updateProfile.and.returnValue(of(mockUser));
      component.form.patchValue({ username: 'john_updated', email: 'new@example.com', password: '' });

      component.onSave();

      expect(userServiceSpy.updateProfile).toHaveBeenCalledWith({ username: 'john_updated', email: 'new@example.com' });
    });

    it('should include password in body when password field is filled', () => {
      userServiceSpy.updateProfile.and.returnValue(of(mockUser));
      component.form.patchValue({ username: 'john', email: 'john@example.com', password: 'newpass123' });

      component.onSave();

      const callArg = userServiceSpy.updateProfile.calls.mostRecent().args[0] as UpdateProfileRequest;
      expect(callArg.password).toBe('newpass123');
    });

    it('should not include password in body when password field is empty', () => {
      userServiceSpy.updateProfile.and.returnValue(of(mockUser));
      component.form.patchValue({ username: 'john', email: 'john@example.com', password: '' });

      component.onSave();

      const callArg = userServiceSpy.updateProfile.calls.mostRecent().args[0] as UpdateProfileRequest;
      expect(callArg.password).toBeUndefined();
    });

    it('should set saved signal to true on success', () => {
      userServiceSpy.updateProfile.and.returnValue(of(mockUser));

      component.onSave();

      expect(component.saved()).toBeTrue();
    });

    it('should reset saved signal to false after 2 seconds', fakeAsync(() => {
      userServiceSpy.updateProfile.and.returnValue(of(mockUser));

      component.onSave();
      expect(component.saved()).toBeTrue();
      tick(2000);
      expect(component.saved()).toBeFalse();
    }));

    it('should set the error signal on failure', () => {
      userServiceSpy.updateProfile.and.returnValue(throwError(() => new Error('Server error')));

      component.onSave();

      expect(component.error()).toBe('Erreur lors de la sauvegarde.');
    });

    it('should clear previous error before a new save attempt', () => {
      userServiceSpy.updateProfile.and.returnValue(of(mockUser));
      component.error.set('Previous error');

      component.onSave();

      expect(component.error()).toBe('');
    });
  });

  describe('unsubscribe', () => {
    beforeEach(() => {
      topicServiceSpy.unsubscribe.and.returnValue(of(void 0));
    });

    it('should call TopicService.unsubscribe with the topic ID', () => {
      component.unsubscribe(mockTopics[0]);
      expect(topicServiceSpy.unsubscribe).toHaveBeenCalledWith(mockTopics[0].id);
    });

    it('should remove the topic from the subscriptions list', () => {
      component.subscriptions.set([mockTopics[0]]);

      component.unsubscribe(mockTopics[0]);

      expect(component.subscriptions().length).toBe(0);
    });

    it('should not remove other topics from the list', () => {
      const anotherTopic: Topic = { id: 3, name: 'TypeScript', description: 'TS lang', subscribed: true };
      component.subscriptions.set([mockTopics[0], anotherTopic]);

      component.unsubscribe(mockTopics[0]);

      expect(component.subscriptions().length).toBe(1);
      expect(component.subscriptions()[0].id).toBe(anotherTopic.id);
    });
  });
});

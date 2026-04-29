import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component } from '@angular/core';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { TopicsComponent } from './topics.component';
import { TopicService } from '../../core/services/topic.service';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { Topic } from '../../shared/models/topic.model';

@Component({ selector: 'app-navbar', template: '', standalone: true })
class NavbarStubComponent {}

describe('TopicsComponent', () => {
  let component: TopicsComponent;
  let fixture: ComponentFixture<TopicsComponent>;
  let topicServiceSpy: jasmine.SpyObj<TopicService>;

  const mockTopics: Topic[] = [
    { id: 1, name: 'Angular', description: 'Angular framework', subscribed: false },
    { id: 2, name: 'Java', description: 'Java programming', subscribed: true }
  ];

  beforeEach(async () => {
    topicServiceSpy = jasmine.createSpyObj('TopicService', ['getAll', 'subscribe', 'unsubscribe']);
    topicServiceSpy.getAll.and.returnValue(of(mockTopics));

    await TestBed.configureTestingModule({
      imports: [TopicsComponent],
      providers: [
        { provide: TopicService, useValue: topicServiceSpy },
        provideRouter([])
      ]
    })
    .overrideComponent(TopicsComponent, {
      remove: { imports: [NavbarComponent] },
      add: { imports: [NavbarStubComponent] }
    })
    .compileComponents();

    fixture = TestBed.createComponent(TopicsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => expect(component).toBeTruthy());

  it('should call TopicService.getAll on init', () => {
    expect(topicServiceSpy.getAll).toHaveBeenCalledTimes(1);
  });

  it('should populate the topics signal with fetched data', () => {
    expect(component.topics()).toEqual(mockTopics);
  });

  it('should set loading to false after topics are loaded', () => {
    expect(component.loading()).toBeFalse();
  });

  it('should set loading to false when fetch fails', () => {
    topicServiceSpy.getAll.and.returnValue(throwError(() => new Error('Network error')));
    const newFixture = TestBed.createComponent(TopicsComponent);
    newFixture.detectChanges();
    expect(newFixture.componentInstance.loading()).toBeFalse();
  });

  describe('toggleSubscription', () => {
    it('should call TopicService.subscribe when the topic is not subscribed', () => {
      topicServiceSpy.subscribe.and.returnValue(of(undefined as any));
      const unsubscribedTopic = { ...mockTopics[0] }; // subscribed: false

      component.toggleSubscription(unsubscribedTopic);

      expect(topicServiceSpy.subscribe).toHaveBeenCalledWith(unsubscribedTopic.id);
    });

    it('should call TopicService.unsubscribe when the topic is already subscribed', () => {
      topicServiceSpy.unsubscribe.and.returnValue(of(undefined as any));
      const subscribedTopic = { ...mockTopics[1] }; // subscribed: true

      component.toggleSubscription(subscribedTopic);

      expect(topicServiceSpy.unsubscribe).toHaveBeenCalledWith(subscribedTopic.id);
    });

    it('should set subscribed to true after subscribing', () => {
      topicServiceSpy.subscribe.and.returnValue(of(undefined as any));
      component.topics.set([...mockTopics]);
      const unsubscribedTopic = { ...mockTopics[0] }; // subscribed: false

      component.toggleSubscription(unsubscribedTopic);

      expect(component.topics().find(t => t.id === unsubscribedTopic.id)?.subscribed).toBeTrue();
    });

    it('should set subscribed to false after unsubscribing', () => {
      topicServiceSpy.unsubscribe.and.returnValue(of(undefined as any));
      component.topics.set([...mockTopics]);
      const subscribedTopic = { ...mockTopics[1] }; // subscribed: true

      component.toggleSubscription(subscribedTopic);

      expect(component.topics().find(t => t.id === subscribedTopic.id)?.subscribed).toBeFalse();
    });

    it('should not alter other topics in the list', () => {
      topicServiceSpy.subscribe.and.returnValue(of(undefined as any));
      component.topics.set([...mockTopics]);
      const unsubscribedTopic = { ...mockTopics[0] };

      component.toggleSubscription(unsubscribedTopic);

      expect(component.topics().find(t => t.id === mockTopics[1].id)?.subscribed).toBe(mockTopics[1].subscribed);
    });
  });
});

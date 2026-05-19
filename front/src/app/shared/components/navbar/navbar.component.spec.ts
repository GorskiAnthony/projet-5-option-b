import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { NavbarComponent } from './navbar.component';
import { AuthService } from '../../../core/services/auth.service';

describe('NavbarComponent', () => {
  let component: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['logout']);

    await TestBed.configureTestingModule({
      imports: [NavbarComponent],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => expect(component).toBeTruthy());

  it('should initialize with menuOpen set to false', () => {
    expect(component.menuOpen()).toBeFalse();
  });

  describe('toggleMenu', () => {
    it('should open the menu when it is closed', () => {
      component.menuOpen.set(false);
      component.toggleMenu();
      expect(component.menuOpen()).toBeTrue();
    });

    it('should close the menu when it is open', () => {
      component.menuOpen.set(true);
      component.toggleMenu();
      expect(component.menuOpen()).toBeFalse();
    });

    it('should toggle correctly on multiple calls', () => {
      component.menuOpen.set(false);
      component.toggleMenu();
      component.toggleMenu();
      expect(component.menuOpen()).toBeFalse();
    });
  });

  describe('closeMenu', () => {
    it('should set menuOpen to false when the menu is open', () => {
      component.menuOpen.set(true);
      component.closeMenu();
      expect(component.menuOpen()).toBeFalse();
    });

    it('should keep menuOpen as false when already closed', () => {
      component.menuOpen.set(false);
      component.closeMenu();
      expect(component.menuOpen()).toBeFalse();
    });
  });

  describe('logout', () => {
    it('should delegate to AuthService.logout', () => {
      component.logout();
      expect(authServiceSpy.logout).toHaveBeenCalledTimes(1);
    });
  });
});

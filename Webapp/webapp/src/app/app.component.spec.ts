import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';
import { ApiService } from './services/api.service';
import { ModelsRestServerStatus } from './fetch/api';
import {By} from "@angular/platform-browser";

describe('AppComponent', () => {

  // Declare our 'mock' object.
  let mockApiService: jasmine.SpyObj<ApiService>;

  // Before each unit test is run, the necessary variables are reset.
  beforeEach(async () => {
    // Initialize our mock object
    mockApiService = jasmine.createSpyObj(ApiService, ['getStatus']);

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
      declarations: [
        AppComponent
      ],
      providers: [
        // Instead of instantiating another component, we pass in our
        // mock object.
        {provide: ApiService, useValue: mockApiService}
      ]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render title', () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const debugElement = fixture.debugElement;

    // Parse the component for the element that has the class '.t-title'
    const titleElement = debugElement.query(By.css('.t-title'));

    // Check that title is not null
    expect(titleElement).toBeTruthy();
    // Check that the title text is correct
    expect(titleElement.nativeElement.innerHTML).toBe('Security Trader');
  });

  it('should make a request to get the status of the server', () => {
    mockApiService.getStatus.and.returnValue(Promise.resolve({
      time: 0,
      address: '0.0.0.0'
    } as ModelsRestServerStatus));

    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();

    expect(mockApiService.getStatus).toHaveBeenCalledTimes(1);
  });

  it('createGame should create a game', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const component = fixture.componentInstance;

    fixture.detectChanges();

    component.createGame();

    // TODO: test create game logic when implemented
  });

  it('joinGame properly join a game', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const component = fixture.componentInstance;

    fixture.detectChanges();

    component.joinGame();

    // TODO: test join game logic when implemented
  })
});

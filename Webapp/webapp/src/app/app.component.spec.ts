import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';
import { ApiService } from './services/api.service';
import { ModelsRestServerStatus } from './fetch/api';

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

  it('should make a request to get the status of the server', () => {
    mockApiService.getStatus.and.returnValue(Promise.resolve({
      time: 0,
      address: '0.0.0.0'
    } as ModelsRestServerStatus));

    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();

    expect(mockApiService.getStatus).toHaveBeenCalledTimes(1);
  });
});

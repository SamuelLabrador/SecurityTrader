import { TestBed } from '@angular/core/testing';
import { RoutesApi, ModelsRestServerStatus, Configuration } from '../fetch/api';
import { ApiService } from './api.service';
import { ConfigurationService } from './configuration.service';

describe('ApiService', () => {
  let mockRoutesApi: jasmine.SpyObj<RoutesApi>;
  let service: ApiService;

  beforeEach(() => {
    // Initalize our mock api
    mockRoutesApi = jasmine.createSpyObj(RoutesApi, ['status']);

    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiService);

    // Instad of invoking the actual api service, we want to invoke our mock object.
    // That we we have control over what the mock object returns.
    service.api = mockRoutesApi;
  });

  it('should be created', () => {
    // Check that service was instantiated properly
    expect(service).toBeTruthy();
  });

  it('getStatus() should properly invoke the RoutesAPI service', async () => {
    // Test preparation
    const expectedResult = {
      time: 0,
      address: '0.0.0.0'
    } as ModelsRestServerStatus;

    // When our mock object is called, we tell it to return the expected result
    // instead of making an actual request to the server
    mockRoutesApi.status.and.returnValue(Promise.resolve(expectedResult));
    
    // Start test and capture result
    const result = await service.getStatus();

    // Check that the result returned equals the expected result
    expect(result).toEqual(expectedResult);
    // Check that the RoutesAPI.status() was invoked 1 time only
    expect(mockRoutesApi.status).toHaveBeenCalledTimes(1);
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChatComponent } from './chat.component';

describe('ChatComponent', () => {
  let component: ChatComponent;
  let fixture: ComponentFixture<ChatComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChatComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should clear the input when clearValue() is invoked', () => {
    component.clearValue();

    expect(component.currentMessage).toBe('');
  })

  it('should add a message to the queue when "enter" is pressed', () => {
    const testMessage = 'hello world';
    component.currentMessage = testMessage;

    // Send enter keystroke event
    component.handleInput(new KeyboardEvent('keystroke', {code: 'Enter'}));

    expect(component.currentMessage).toBe('');
    expect(component.messageList[0]).toBe(testMessage);
  })

  it('should not invoke clearValue() when a key that is not "enter" is pressed', () => {
    const testMessage = 'hello world';
    component.currentMessage = testMessage;

    component.handleInput(new KeyboardEvent('keystroke', {code: 'Backspace'}));

    expect(component.currentMessage).toBe(testMessage);
    expect(component.messageList.length).toBe(0);


  })
});

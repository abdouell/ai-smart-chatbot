// chat-widget.component.ts
import { Component, OnInit, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { HttpClient, HttpClientModule, HttpErrorResponse } from '@angular/common/http';
import { first, Observable, of, tap, timeout } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule, DatePipe, NgClass } from '@angular/common';
import { FaqResponse } from '../model/faq-response';
import { FaqItem } from '../model/faq-item';

// Extended FaqItem interface to include open/close state
interface DisplayFaqItem extends FaqItem {
  isOpen?: boolean;
}

// Extended Message interface to include feedback properties
interface Message {
  content: string;
  sender: 'user' | 'bot';
  timestamp: Date;
  relatedFaqs?: DisplayFaqItem[]; // Property to store related FAQs
  isAccordionOpen?: boolean; // Property to track accordion state

  // Feedback properties
  feedbackGiven?: boolean;
  feedbackPositive?: boolean;
  feedbackTimestamp?: Date;
  detailedFeedback?: string;
  detailedFeedbackGiven?: boolean;
  messageId?: string; // Unique identifier for the message for feedback tracking
}

// Feedback interface for sending to backend
interface FeedbackData {
  messageId: string;
  questionText: string;
  responseText: string;
  isPositive: boolean;
  detailedFeedback?: string;
  timestamp: Date;
}

@Component({
  selector: 'app-chat-widget',
  templateUrl: './chat-widget.component.html',
  styleUrls: ['./chat-widget.component.scss'],
  imports: [
    FormsModule,  // Required for ngModel
    HttpClientModule,  // Required for HTTP requests
    // Other modules you might be using
    FormsModule,
    DatePipe,
    NgClass,
    CommonModule,
  ],
})
export class ChatWidgetComponent implements OnInit, AfterViewChecked {
  isOpen = false;
  messages: Message[] = [];
  newMessage = '';
  isLoading = false;

  // Initialize with the ! non-null assertion operator
  @ViewChild('scrollMe') private messagesContainer!: ElementRef;

  // Replace with your actual API endpoints
  private apiUrl = 'http://localhost:8092/api/faq/ask';
  private feedbackUrl = 'http://localhost:8092/api/message/feed-back';

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    // You could add a welcome message here
    this.addBotMessage('Bonjour ! Je suis là pour vous aider 🙂');
  }

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  scrollToBottom(): void {
    try {
      this.messagesContainer.nativeElement.scrollTop = this.messagesContainer.nativeElement.scrollHeight;
    } catch(err) { }
  }

  toggleChat(): void {
    this.isOpen = !this.isOpen;
    if (this.isOpen && this.messages.length === 0) {
      this.addBotMessage('Hi there! How can I help you today?');
    }
  }

  sendMessage(): void {
    if (!this.newMessage.trim()) return;

    // Add user message to chat
    this.addUserMessage(this.newMessage);
    const userQuery = this.newMessage;
    this.newMessage = '';
    this.isLoading = true;

    // Call backend API
    this.getResponseFromBackend(userQuery).pipe(
      first(),
      tap(response => {
        console.log(response);
        this.isLoading = false;

        // Prepare the related FAQs with isOpen property
        const displayFaqs = response.nearFaqItems.map(faq => ({
          ...faq,
          isOpen: false // All FAQs are initially closed
        }));

        // Pass both the response text and the enhanced nearFaqItems
        this.addBotMessage(response.response || 'I found this information for you.',
          displayFaqs,
          response.messageId
        );
      })
    ).subscribe({
      error: (error) => {
        this.isLoading = false;
        console.error('Error fetching response:', error);
        this.addBotMessage("Oups, je n'ai pas pu traiter votre demande. Veuillez réessayer plus tard.");
      }
    });
  }

  private addUserMessage(content: string): void {
    this.messages.push({
      content,
      sender: 'user',
      timestamp: new Date(),
      messageId: this.generateMessageId()
    });
  }

  private addBotMessage(content: string, relatedFaqs?: DisplayFaqItem[], messageId?: number): void {
    this.messages.push({
      content,
      sender: 'bot',
      timestamp: new Date(),
      relatedFaqs,
      isAccordionOpen: false, // Accordion is closed by default
      feedbackGiven: false,
      messageId: messageId?.toString() || this.generateMessageId()
    });
  }

  private getResponseFromBackend(query: string): Observable<FaqResponse> {
    console.log('Sending request to:', this.apiUrl);
    return this.http.get<FaqResponse>(this.apiUrl + '?question=' + query);
  }

  // Method to toggle the main accordion open/closed state
  toggleAccordion(message: Message): void {
    message.isAccordionOpen = !message.isAccordionOpen;
  }

  // Method to toggle a specific FAQ answer visibility
  toggleFaqAnswer(message: Message, faqIndex: number): void {
    if (message.relatedFaqs && message.relatedFaqs[faqIndex]) {
      message.relatedFaqs[faqIndex].isOpen = !message.relatedFaqs[faqIndex].isOpen;
    }
  }

  // Generate a unique message ID for tracking feedback
  private generateMessageId(): string {
    return Date.now().toString(36) + Math.random().toString(36).substr(2, 5);
  }

  // Handle user feedback
  giveFeedback(message: Message, isPositive: boolean): void {
    message.feedbackGiven = true;
    message.feedbackPositive = isPositive;
    message.feedbackTimestamp = new Date();

    // Find the preceding user message to include in feedback data
    let userQuestion = '';
    const messageIndex = this.messages.findIndex(m => m.messageId === message.messageId);
    if (messageIndex > 0 && this.messages[messageIndex - 1].sender === 'user') {
      userQuestion = this.messages[messageIndex - 1].content;
    }

    // Prepare feedback data for submission
    const feedbackData: FeedbackData = {
      messageId: message.messageId || '',
      questionText: userQuestion,
      responseText: message.content,
      isPositive: isPositive,
      timestamp: message.feedbackTimestamp
    };

    // Send the feedback to the backend
    this.submitFeedback(feedbackData);

    // If negative feedback, we might want to get more details, but no need to wait for the API call
    // The UI will show the detailed feedback form based on message.feedbackPositive
  }

  // Submit detailed feedback (for negative responses)
  submitDetailedFeedback(message: Message): void {
    if (!message.detailedFeedback?.trim()) {
      // Skip if no detailed feedback is provided
      this.skipDetailedFeedback(message);
      return;
    }

    message.detailedFeedbackGiven = true;

    // Prepare feedback data with detailed comments
    const feedbackData: FeedbackData = {
      messageId: message.messageId || '',
      questionText: '', // We already have this from the initial feedback
      responseText: message.content,
      isPositive: false, // Detailed feedback is only collected for negative responses
      detailedFeedback: message.detailedFeedback,
      timestamp: new Date()
    };

    // Submit the detailed feedback
    this.submitDetailedFeedback2(feedbackData);
  }

  // Skip providing detailed feedback
  skipDetailedFeedback(message: Message): void {
    message.detailedFeedbackGiven = true;
    // We can still mark this in our analytics that the user chose to skip
  }

  // Send feedback to the backend API
  private submitFeedback(feedbackData: FeedbackData): void {
    console.log('submitFeedback');
    console.log(feedbackData);
    this.http.post(this.feedbackUrl, feedbackData)
      .pipe(
        catchError((error) => {
          console.error('Error submitting feedback:', error);
          return of(null); // Return observable with null to continue the stream
        })
      )
      .subscribe();
  }

  // Send detailed feedback to the backend API
  private submitDetailedFeedback2(feedbackData: FeedbackData): void {
    console.log('submitDetailedFeedback2:');
    console.log(feedbackData);
    this.http.post(this.feedbackUrl, feedbackData)
      .pipe(
        catchError((error) => {
          console.error('Error submitting detailed feedback:', error);
          return of(null);
        })
      )
      .subscribe();
  }
}

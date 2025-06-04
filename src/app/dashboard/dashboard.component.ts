import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MessageService } from '../services/message.service';
import { Message } from '../model/message';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  messages: Message[] = [];
  messagesWithFeedback: Message[] = [];
  loading = false;
  error = '';

  constructor(private messageService: MessageService) {}

  ngOnInit(): void {
    this.loadMessages();
    this.loadMessagesWithFeedback();
  }

  loadMessages(): void {
    this.loading = true;
    this.messageService.getAllMessages().subscribe({
      next: (data) => {
        this.messages = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load messages';
        console.error(err);
        this.loading = false;
      }
    });
  }

  loadMessagesWithFeedback(): void {
    this.messageService.getMessagesWithFeedback().subscribe({
      next: (data) => {
        this.messagesWithFeedback = data;
      },
      error: (err) => {
        console.error('Failed to load messages with feedback', err);
      }
    });
  }
}

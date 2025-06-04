import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MessageService } from '../services/message.service';
import { Message } from '../model/message';


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  // Original data
  allMessages: Message[] = [];
  allMessagesWithFeedback: Message[] = [];

  // Filtered and paginated data
  messages: Message[] = [];
  messagesWithFeedback: Message[] = [];

  // Loading and error states
  loading = false;
  error = '';

  // Pagination
  currentPage = 1;
  itemsPerPage = 6;
  totalPages = 1;

  // Pagination for messages with feedback
  currentPageFeedback = 1;
  totalPagesFeedback = 1;

  // Filters
  filterText = '';
  filterDateFrom: string = '';
  filterDateTo: string = '';
  filterFeedbackType: string = 'all'; // 'all', 'positive', 'negative'
  filterTopicType: string = 'all'; // 'all', 'on-topic', 'off-topic'

  // Statistics
  stats = {
    totalMessages: 0,
    messagesWithFeedback: 0,
    positivePercentage: 0,
    negativePercentage: 0,
    onTopicPercentage: 0,
    offTopicPercentage: 0
  };

  constructor(private messageService: MessageService) {}

  ngOnInit(): void {
    this.loadMessages();
    this.loadMessagesWithFeedback();
  }

  loadMessages(): void {
    this.loading = true;
    this.messageService.getAllMessages().subscribe({
      next: (data) => {
        this.allMessages = data;
        this.calculateStats();
        this.applyFilters();
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
        this.allMessagesWithFeedback = data;
        this.applyFiltersFeedback();
        this.calculateStats();
      },
      error: (err) => {
        console.error('Failed to load messages with feedback', err);
      }
    });
  }

  // Pagination methods
  setPage(page: number): void {
    if (page < 1 || page > this.totalPages) {
      return;
    }
    this.currentPage = page;
    this.applyFilters();
  }

  setPageFeedback(page: number): void {
    if (page < 1 || page > this.totalPagesFeedback) {
      return;
    }
    this.currentPageFeedback = page;
    this.applyFiltersFeedback();
  }

  // Generate array of page numbers for pagination UI
  getPages(total: number): number[] {
    return Array.from({ length: total }, (_, i) => i + 1);
  }

  // Filter methods
  applyFilters(): void {
    let filtered = [...this.allMessages];

    // Apply text filter
    if (this.filterText) {
      const searchText = this.filterText.toLowerCase();
      filtered = filtered.filter(message =>
        message.userQuestion.toLowerCase().includes(searchText) ||
        message.reponse.toLowerCase().includes(searchText) ||
        (message.feedback && message.feedback.toLowerCase().includes(searchText))
      );
    }

    // Apply date filters
    if (this.filterDateFrom) {
      const fromDate = new Date(this.filterDateFrom);
      filtered = filtered.filter(message => new Date(message.timestamp) >= fromDate);
    }

    if (this.filterDateTo) {
      const toDate = new Date(this.filterDateTo);
      toDate.setHours(23, 59, 59, 999); // End of the day
      filtered = filtered.filter(message => new Date(message.timestamp) <= toDate);
    }

    // Apply feedback type filter
    if (this.filterFeedbackType !== 'all') {
      if (this.filterFeedbackType === 'positive') {
        filtered = filtered.filter(message => message.feedBackPositive === true);
      } else if (this.filterFeedbackType === 'negative') {
        filtered = filtered.filter(message => message.feedBackPositive === false);
      }
    }

    // Apply topic type filter
    if (this.filterTopicType !== 'all') {
      if (this.filterTopicType === 'on-topic') {
        filtered = filtered.filter(message => !message.horsSujet);
      } else if (this.filterTopicType === 'off-topic') {
        filtered = filtered.filter(message => message.horsSujet);
      }
    }

    // Calculate total pages
    this.totalPages = Math.max(1, Math.ceil(filtered.length / this.itemsPerPage));

    // Adjust current page if needed
    if (this.currentPage > this.totalPages) {
      this.currentPage = this.totalPages;
    }

    // Apply pagination
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    this.messages = filtered.slice(startIndex, startIndex + this.itemsPerPage);
  }

  applyFiltersFeedback(): void {
    let filtered = [...this.allMessagesWithFeedback];

    // Apply text filter
    if (this.filterText) {
      const searchText = this.filterText.toLowerCase();
      filtered = filtered.filter(message =>
        message.userQuestion.toLowerCase().includes(searchText) ||
        message.reponse.toLowerCase().includes(searchText) ||
        (message.feedback && message.feedback.toLowerCase().includes(searchText))
      );
    }

    // Apply date filters
    if (this.filterDateFrom) {
      const fromDate = new Date(this.filterDateFrom);
      filtered = filtered.filter(message => new Date(message.timestamp) >= fromDate);
    }

    if (this.filterDateTo) {
      const toDate = new Date(this.filterDateTo);
      toDate.setHours(23, 59, 59, 999); // End of the day
      filtered = filtered.filter(message => new Date(message.timestamp) <= toDate);
    }

    // Apply feedback type filter
    if (this.filterFeedbackType !== 'all') {
      if (this.filterFeedbackType === 'positive') {
        filtered = filtered.filter(message => message.feedBackPositive === true);
      } else if (this.filterFeedbackType === 'negative') {
        filtered = filtered.filter(message => message.feedBackPositive === false);
      }
    }

    // Apply topic type filter
    if (this.filterTopicType !== 'all') {
      if (this.filterTopicType === 'on-topic') {
        filtered = filtered.filter(message => !message.horsSujet);
      } else if (this.filterTopicType === 'off-topic') {
        filtered = filtered.filter(message => message.horsSujet);
      }
    }

    // Calculate total pages
    this.totalPagesFeedback = Math.max(1, Math.ceil(filtered.length / this.itemsPerPage));

    // Adjust current page if needed
    if (this.currentPageFeedback > this.totalPagesFeedback) {
      this.currentPageFeedback = this.totalPagesFeedback;
    }

    // Apply pagination
    const startIndex = (this.currentPageFeedback - 1) * this.itemsPerPage;
    this.messagesWithFeedback = filtered.slice(startIndex, startIndex + this.itemsPerPage);
  }

  // Reset all filters
  resetFilters(): void {
    this.filterText = '';
    this.filterDateFrom = '';
    this.filterDateTo = '';
    this.filterFeedbackType = 'all';
    this.filterTopicType = 'all';
    this.currentPage = 1;
    this.currentPageFeedback = 1;
    this.applyFilters();
    this.applyFiltersFeedback();
  }

  // Calculate statistics
  calculateStats(): void {
    // Total messages
    this.stats.totalMessages = this.allMessages.length;

    // Messages with feedback
    this.stats.messagesWithFeedback = this.allMessagesWithFeedback.length;

    // Feedback percentages
    const positiveCount = this.allMessages.filter(m => m.feedBackPositive === true).length;
    const negativeCount = this.allMessages.filter(m => m.feedBackPositive === false).length;

    this.stats.positivePercentage = this.stats.messagesWithFeedback > 0
      ? Math.round((positiveCount / this.stats.messagesWithFeedback) * 100)
      : 0;

    this.stats.negativePercentage = this.stats.messagesWithFeedback > 0
      ? Math.round((negativeCount / this.stats.messagesWithFeedback) * 100)
      : 0;

    // Topic percentages
    const onTopicCount = this.allMessages.filter(m => !m.horsSujet).length;
    const offTopicCount = this.allMessages.filter(m => m.horsSujet).length;

    this.stats.onTopicPercentage = this.stats.totalMessages > 0
      ? Math.round((onTopicCount / this.stats.totalMessages) * 100)
      : 0;

    this.stats.offTopicPercentage = this.stats.totalMessages > 0
      ? Math.round((offTopicCount / this.stats.totalMessages) * 100)
      : 0;
  }
}

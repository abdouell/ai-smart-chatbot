import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FaqService } from '../services/faq.service';
import { FaqItem } from '../model/faq-item';

@Component({
  selector: 'app-faq-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './faq-management.component.html',
  styleUrls: ['./faq-management.component.scss']
})
export class FaqManagementComponent implements OnInit {
  // All FAQ items
  allFaqItems: FaqItem[] = [];

  // Paginated FAQ items
  faqItems: FaqItem[] = [];

  // Loading and error states
  loading = false;
  error = '';

  // Pagination
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;

  // Search filter
  searchText = '';

  // Form data for create/edit
  formMode: 'create' | 'edit' = 'create';
  currentFaqItem: FaqItem = { question: '', reponse: '' };
  showForm = false;

  constructor(private faqService: FaqService) {}

  ngOnInit(): void {
    this.loadFaqItems();
  }

  loadFaqItems(): void {
    this.loading = true;
    this.faqService.getAllFaqItems().subscribe({
      next: (data) => {
        this.allFaqItems = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load FAQ items';
        console.error(err);
        this.loading = false;
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

  // Generate array of page numbers for pagination UI
  getPages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  // Filter methods
  applyFilters(): void {
    let filtered = [...this.allFaqItems];

    // Apply text filter
    if (this.searchText) {
      const searchText = this.searchText.toLowerCase();
      filtered = filtered.filter(item =>
        item.question.toLowerCase().includes(searchText) ||
        item.reponse.toLowerCase().includes(searchText)
      );
    }

    // Calculate total pages
    this.totalPages = Math.max(1, Math.ceil(filtered.length / this.itemsPerPage));

    // Adjust current page if needed
    if (this.currentPage > this.totalPages) {
      this.currentPage = this.totalPages;
    }

    // Apply pagination
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    this.faqItems = filtered.slice(startIndex, startIndex + this.itemsPerPage);
  }

  // Reset search filter
  resetFilters(): void {
    this.searchText = '';
    this.currentPage = 1;
    this.applyFilters();
  }

  // Form methods
  openCreateForm(): void {
    this.formMode = 'create';
    this.currentFaqItem = { question: '', reponse: '' };
    this.showForm = true;
  }

  openEditForm(faqItem: FaqItem): void {
    this.formMode = 'edit';
    this.currentFaqItem = { ...faqItem };
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
  }

  submitForm(): void {
    if (this.formMode === 'create') {
      this.createFaqItem();
    } else {
      this.updateFaqItem();
    }
  }

  createFaqItem(): void {
    this.loading = true;
    this.faqService.createFaqItem(this.currentFaqItem).subscribe({
      next: () => {
        this.loadFaqItems();
        this.closeForm();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to create FAQ item';
        console.error(err);
        this.loading = false;
      }
    });
  }

  updateFaqItem(): void {
    if (!this.currentFaqItem.id) {
      this.error = 'Cannot update FAQ item without ID';
      return;
    }

    this.loading = true;
    this.faqService.updateFaqItem(this.currentFaqItem.id, this.currentFaqItem).subscribe({
      next: () => {
        this.loadFaqItems();
        this.closeForm();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to update FAQ item';
        console.error(err);
        this.loading = false;
      }
    });
  }

  deleteFaqItem(id: number): void {
    if (confirm('Are you sure you want to delete this FAQ item?')) {
      this.loading = true;
      this.faqService.deleteFaqItem(id).subscribe({
        next: () => {
          this.loadFaqItems();
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to delete FAQ item';
          console.error(err);
          this.loading = false;
        }
      });
    }
  }
}

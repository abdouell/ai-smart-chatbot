import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FaqItem } from '../model/faq-item';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FaqService {
  private apiUrl = `${environment.apiUrl}/faq`;

  constructor(private http: HttpClient) { }

  getAllFaqItems(): Observable<FaqItem[]> {
    return this.http.get<FaqItem[]>(this.apiUrl);
  }

  getFaqItemById(id: number): Observable<FaqItem> {
    return this.http.get<FaqItem>(`${this.apiUrl}/${id}`);
  }

  createFaqItem(faqItem: FaqItem): Observable<FaqItem> {
    return this.http.post<FaqItem>(this.apiUrl, faqItem);
  }

  updateFaqItem(id: number, faqItem: FaqItem): Observable<FaqItem> {
    return this.http.put<FaqItem>(`${this.apiUrl}/${id}`, faqItem);
  }

  deleteFaqItem(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

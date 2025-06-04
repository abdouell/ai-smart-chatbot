import {FaqItem} from './faq-item';

export interface FaqResponse {
  messageId: number;
  response: string;
  nearFaqItems: FaqItem[];
}

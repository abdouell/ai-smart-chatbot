export interface Message {
  id: number;
  userQuestion: string;
  timestamp: string;
  reponse: string;
  faqSimilaires: string;
  feedback: string | null;
  feedBackPositive: boolean | null;
  horsSujet: boolean;
}

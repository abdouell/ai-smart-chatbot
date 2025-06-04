export interface FaqItem {
  id?: number;             // optionnel si l'objet est encore non persisté
  question: string;
  reponse: string;
  embedding?: string;      // optionnel si non utilisé côté front
}

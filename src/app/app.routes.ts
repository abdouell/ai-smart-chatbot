import { Routes } from '@angular/router';
import {FaqComponent} from './faq/faq.component';
import {ChatWidgetComponent} from './chat-widget/chat-widget.component';
import {DashboardComponent} from './dashboard/dashboard.component';

export const routes: Routes = [
  {
    path: 'faq',
    component: FaqComponent
  },
  {
    path: 'chat',
    component: ChatWidgetComponent
  },
  {
    path: 'dashboard',
    component: DashboardComponent
  }
];

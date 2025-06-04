import { Component } from '@angular/core';
import {ChatWidgetComponent} from '../chat-widget/chat-widget.component';

@Component({
  selector: 'app-faq',
  imports: [
    ChatWidgetComponent
  ],
  templateUrl: './faq.component.html',
  styleUrl: './faq.component.scss'
})
export class FaqComponent {

}

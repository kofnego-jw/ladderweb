import { Component, Input, OnInit } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';

@Component({
  selector: 'app-error-modal',
  templateUrl: './error-modal.component.html',
  styleUrls: ['./error-modal.component.scss'],
})
export class ErrorModalComponent implements OnInit {
  @Input() message: string = '';

  constructor(private modalRef: BsModalRef) {}

  addMessage(message: string): void {
    this.message = this.message + '<br/>' + message;
  }

  ngOnInit(): void {}

  close(): void {
    this.modalRef.hide();
  }
}

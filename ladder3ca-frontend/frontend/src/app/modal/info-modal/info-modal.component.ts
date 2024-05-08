import { Component, Input, OnInit } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';

@Component({
  selector: 'app-info-modal',
  templateUrl: './info-modal.component.html',
  styleUrls: ['./info-modal.component.scss'],
})
export class InfoModalComponent implements OnInit {
  @Input()
  title: string = '';
  @Input()
  message: string = '';

  constructor(private modalRef: BsModalRef) {}

  ngOnInit(): void {}

  close(): void {
    this.modalRef.hide();
  }
}

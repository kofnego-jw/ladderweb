import { Component, Input, OnInit } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-confirmation-modal',
  templateUrl: './confirmation-modal.component.html',
  styleUrls: ['./confirmation-modal.component.scss'],
})
export class ConfirmationModalComponent implements OnInit {
  @Input()
  title: string = '';
  @Input()
  message: string = '';
  @Input()
  options: string[] = [];

  confirmedWith$: Subject<string | undefined> = new Subject();

  constructor(private modalRef: BsModalRef) {}

  ngOnDestroy(): void {
    if (!this.confirmedWith$.closed) {
      this.close();
    }
  }

  ngOnInit(): void {}

  select(option: string | undefined): void {
    this.confirmedWith$.next(option);
    this.confirmedWith$.complete();
    this.modalRef.hide();
  }

  close(): void {
    this.select(undefined);
  }
}

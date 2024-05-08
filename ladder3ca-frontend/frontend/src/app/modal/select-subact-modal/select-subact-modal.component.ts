import { Component, Input, OnInit } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { Subject } from 'rxjs';
import { SubactDTO } from 'src/app/model';

@Component({
  selector: 'app-select-subact-modal',
  templateUrl: './select-subact-modal.component.html',
  styleUrls: ['./select-subact-modal.component.scss'],
})
export class SelectSubactModalComponent implements OnInit {
  @Input()
  token: string = '';

  @Input()
  subactList: SubactDTO[] = [];

  @Input()
  selectedSubacts: SubactDTO[] = [];

  subactsUpdate$: Subject<SubactDTO[]> = new Subject();

  constructor(private modalRef: BsModalRef) {}

  ngOnInit(): void {}

  close(): void {
    this.subactsUpdate$.complete();
    this.modalRef.hide();
  }

  isActive(subact: SubactDTO): boolean {
    return this.selectedSubacts.includes(subact);
  }

  toggleSubact(subact: SubactDTO): void {
    if (this.selectedSubacts.includes(subact)) {
      this.selectedSubacts = this.selectedSubacts.filter((m) => m !== subact);
    } else {
      this.selectedSubacts.push(subact);
    }
  }

  updateSubacts(): void {
    this.subactsUpdate$.next(this.selectedSubacts);
    this.close();
  }
}

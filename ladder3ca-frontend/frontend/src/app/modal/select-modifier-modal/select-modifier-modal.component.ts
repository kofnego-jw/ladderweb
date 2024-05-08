import { Component, Input, OnInit } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { Subject } from 'rxjs';
import { ModifierDTO } from 'src/app/model';

@Component({
  selector: 'app-select-modifier-modal',
  templateUrl: './select-modifier-modal.component.html',
  styleUrls: ['./select-modifier-modal.component.scss'],
})
export class SelectModifierModalComponent implements OnInit {
  @Input()
  token: string = '';

  @Input()
  modifierList: ModifierDTO[] = [];

  @Input()
  selectedModifiers: ModifierDTO[] = [];

  modifiersUpdate$: Subject<ModifierDTO[]> = new Subject();

  constructor(private modalRef: BsModalRef) {}

  ngOnInit(): void {}

  close(): void {
    this.modifiersUpdate$.complete();
    this.modalRef.hide();
  }

  isActive(modifier: ModifierDTO): boolean {
    return this.selectedModifiers.includes(modifier);
  }

  toggleModifier(modifier: ModifierDTO): void {
    if (this.selectedModifiers.includes(modifier)) {
      this.selectedModifiers = this.selectedModifiers.filter(
        (m) => m !== modifier
      );
    } else {
      this.selectedModifiers.push(modifier);
    }
  }

  updateModifiers(): void {
    this.modifiersUpdate$.next(this.selectedModifiers);
    this.close();
  }
}

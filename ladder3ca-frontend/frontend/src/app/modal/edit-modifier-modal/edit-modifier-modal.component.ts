import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { Subject } from 'rxjs';
import { ModifierDTO } from 'src/app/model';

@Component({
  selector: 'app-edit-modifier-modal',
  templateUrl: './edit-modifier-modal.component.html',
  styleUrls: ['./edit-modifier-modal.component.scss'],
})
export class EditModifierModalComponent implements OnInit {
  @Input()
  modifier: ModifierDTO | undefined;

  modifierForm: FormGroup;

  modifierUpdate$: Subject<ModifierDTO | undefined> = new Subject();

  constructor(fb: FormBuilder, private modalRef: BsModalRef) {
    this.modifierForm = fb.group({
      name: [],
      desc: [],
    });
  }

  ngOnInit(): void {
    if (this.modifier) {
      this.modifierForm.get('name')?.setValue(this.modifier.modifierCode);
      this.modifierForm.get('desc')?.setValue(this.modifier.desc);
    }
  }

  close(): void {
    this.modifierUpdate$.complete();
    this.modalRef.hide();
  }

  updateModifier(): void {
    if (this.modifierForm.touched && this.modifierForm.valid) {
      const id = this.modifier ? this.modifier.id : undefined;
      const modifierCode = this.modifierForm.get('name')?.value;
      const desc = this.modifierForm.get('desc')?.value;
      const updated = new ModifierDTO(id, modifierCode, desc);
      this.modifierUpdate$.next(updated);
    }
    this.close();
  }
}

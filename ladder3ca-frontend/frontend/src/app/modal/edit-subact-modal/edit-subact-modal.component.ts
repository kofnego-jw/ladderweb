import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { Subject } from 'rxjs';
import { SubactDTO } from 'src/app/model';

@Component({
  selector: 'app-edit-subact-modal',
  templateUrl: './edit-subact-modal.component.html',
  styleUrls: ['./edit-subact-modal.component.scss'],
})
export class EditSubactModalComponent implements OnInit {
  @Input()
  subact: SubactDTO | undefined;

  @Input()
  parents: SubactDTO[] = [];

  subactForm: FormGroup;

  subactUpdate$: Subject<SubactDTO | undefined> = new Subject();

  constructor(fb: FormBuilder, private modalRef: BsModalRef) {
    this.subactForm = fb.group({
      name: [],
      desc: [],
      parent: [],
    });
  }

  ngOnInit(): void {
    if (this.subact) {
      this.subactForm.get('name')?.setValue(this.subact.subactName);
      this.subactForm.get('desc')?.setValue(this.subact.desc);
      this.subactForm.get('parent')?.setValue(this.subact.parentSubactId);
    }
  }

  close(): void {
    this.subactUpdate$.complete();
    this.modalRef.hide();
  }

  updateSubact(): void {
    if (this.subactForm.touched && this.subactForm.valid) {
      const id = this.subact ? this.subact.id : undefined;
      const subactCode = this.subactForm.get('name')?.value;
      const desc = this.subactForm.get('desc')?.value;
      const parentSubactId = this.subactForm.get('parent')?.value;
      const updated = new SubactDTO(
        id,
        subactCode,
        parentSubactId,
        undefined,
        desc
      );
      this.subactUpdate$.next(updated);
    }
    this.close();
  }
}

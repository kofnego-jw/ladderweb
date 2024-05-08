import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { Subject } from 'rxjs';
import { CreationTaskDTO } from 'src/app/model';

@Component({
  selector: 'app-edit-creation-task-modal',
  templateUrl: './edit-creation-task-modal.component.html',
  styleUrls: ['./edit-creation-task-modal.component.scss'],
})
export class EditCreationTaskModalComponent implements OnInit {
  @Input()
  creationTask: CreationTaskDTO | undefined;

  creationTaskForm: FormGroup;

  creationTaskUpdate$: Subject<CreationTaskDTO | undefined> = new Subject();

  constructor(fb: FormBuilder, private modalRef: BsModalRef) {
    this.creationTaskForm = fb.group({
      name: [],
      desc: [],
      category: [],
    });
  }

  ngOnInit(): void {
    if (this.creationTask) {
      this.creationTaskForm.get('name')?.setValue(this.creationTask.taskName);
      this.creationTaskForm.get('desc')?.setValue(this.creationTask.desc);
      this.creationTaskForm
        .get('category')
        ?.setValue(this.creationTask.category);
    }
  }

  close(): void {
    this.creationTaskUpdate$.complete();
    this.modalRef.hide();
  }

  updateCreationTask(): void {
    if (this.creationTaskForm.touched && this.creationTaskForm.valid) {
      const id = this.creationTask ? this.creationTask.id : undefined;
      const taskName = this.creationTaskForm.get('name')?.value;
      const desc = this.creationTaskForm.get('desc')?.value;
      const category = this.creationTaskForm.get('category')?.value;
      const updated = new CreationTaskDTO(id, taskName, desc, category);
      this.creationTaskUpdate$.next(updated);
    }
    this.close();
  }
}

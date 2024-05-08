import { AfterViewInit, Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { Subject } from 'rxjs';
import {
  CreationTaskDTO,
  Language,
  SpeakerGender,
  TextWithMetadataDTO,
} from 'src/app/model';

@Component({
  selector: 'app-edit-text-with-metadata-modal',
  templateUrl: './edit-text-with-metadata-modal.component.html',
  styleUrls: ['./edit-text-with-metadata-modal.component.scss'],
})
export class EditTextWithMetadataModalComponent implements OnInit {
  genders: SpeakerGender[] = [
    SpeakerGender.F,
    SpeakerGender.M,
    SpeakerGender.D,
  ];
  @Input()
  text: TextWithMetadataDTO | undefined;

  @Input()
  creationTaskList: CreationTaskDTO[] = [];

  textForm: FormGroup;

  textUpdate$: Subject<TextWithMetadataDTO | undefined> = new Subject();

  languages: string[] = [Language.de, Language.it];

  constructor(fb: FormBuilder, private modalRef: BsModalRef) {
    this.textForm = fb.group({
      altId: [],
      textdata: [],
      languageCode: [],
      creationTask: [],
      gender: [],
      ageAtCreation: [],
      l1language: [],
      l2languages: [],
      location: [],
    });
  }

  ngOnInit(): void {
    this.setFormValues();
  }

  setFormValues(): void {
    if (this.text) {
      const task = this.text.creationTask
        ? this.creationTaskList.find(
            (x) => x.id === this.text?.creationTask?.id
          )
        : undefined;
      this.textForm.get('altId')?.setValue(this.text.altId);
      this.textForm.get('textdata')?.setValue(this.text.textdata);
      this.textForm.get('languageCode')?.setValue(this.text.languageCode);
      this.textForm.get('creationTask')?.setValue(task);
      this.textForm.get('gender')?.setValue(this.text.gender);
      this.textForm.get('ageAtCreation')?.setValue(this.text.ageAtCreation);
      this.textForm.get('l1language')?.setValue(this.text.l1Language);
      this.textForm.get('l2languages')?.setValue(this.text.l2Languages);
      this.textForm.get('location')?.setValue(this.text.location);
    }
  }

  close(): void {
    this.textUpdate$.complete();
    this.modalRef.hide();
  }

  updateText(): void {
    if (this.textForm.touched && this.textForm.valid) {
      const id = this.text ? this.text.id : undefined;
      const altId = this.textForm.get('altId')?.value;
      const textdata = this.textForm.get('textdata')?.value;
      const languageCode = this.textForm.get('languageCode')?.value;
      const creationTask = this.textForm.get('creationTask')?.value;
      const gender = this.textForm.get('gender')?.value;
      const ageAtCreation = this.textForm.get('ageAtCreation')?.value;
      const l1language = this.textForm.get('l1language')?.value;
      const l2languages = this.textForm.get('l2languages')?.value;
      const location = this.textForm.get('location')?.value;
      const updated = new TextWithMetadataDTO(
        id,
        altId,
        textdata,
        languageCode,
        creationTask,
        0,
        gender,
        ageAtCreation,
        l1language,
        l2languages,
        location,
        []
      );
      this.textUpdate$.next(updated);
    }
    this.close();
  }
}

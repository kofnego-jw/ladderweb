import { Injectable } from '@angular/core';
import { AppService } from './app.service';
import { MyModalService } from './my-modal.service';
import { Router } from '@angular/router';
import { CreationTaskDTO, TextDTO, TextWithMetadataDTO } from '../model';
import { ReplaySubject, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class EditTextStateService {
  creationTaskList: CreationTaskDTO[] = [];

  currentText$: ReplaySubject<TextWithMetadataDTO | undefined> =
    new ReplaySubject();

  constructor(
    private app: AppService,
    private modal: MyModalService,
    private router: Router
  ) {
    this.app.ct_listAll().subscribe((list) => (this.creationTaskList = list));
  }

  addToDatabase(text: string): void {
    const dto = new TextWithMetadataDTO(
      undefined,
      '',
      text,
      '',
      undefined,
      0,
      undefined,
      0,
      '',
      '',
      '',
      []
    );
    this.modal.editText(dto, this.creationTaskList).subscribe((dto) => {
      if (dto) {
        this.app
          .txt_addNew(
            dto.altId,
            dto.textdata,
            dto.languageCode,
            dto.creationTask,
            dto.gender,
            dto.ageAtCreation,
            dto.l1Language,
            dto.l2Languages,
            dto.location
          )
          .subscribe((dto) => this.gotoPage('/database/text'));
      }
    });
  }

  annotateText(text: TextDTO): void {
    if (text.id) {
      this.app.txt_readOne(text.id!).subscribe((dto) => {
        if (dto) {
          this.currentText$.next(dto);
          this.gotoPage('/database/text/annotation');
        }
      });
    }
  }

  gotoPage(url: string): void {
    this.router.navigate([url]);
  }
}

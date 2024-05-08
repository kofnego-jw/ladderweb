import { Injectable } from '@angular/core';
import { BsModalService } from 'ngx-bootstrap/modal';
import { AppService } from './app.service';
import {
  CreationTaskDTO,
  ModifierDTO,
  SearchClause,
  SubactDTO,
  TextWithMetadataDTO,
} from '../model';
import { Observable, of } from 'rxjs';
import { EditCreationTaskModalComponent } from '../modal/edit-creation-task-modal/edit-creation-task-modal.component';
import { ConfirmationModalComponent } from '../modal/confirmation-modal/confirmation-modal.component';
import { EditModifierModalComponent } from '../modal/edit-modifier-modal/edit-modifier-modal.component';
import { EditSubactModalComponent } from '../modal/edit-subact-modal/edit-subact-modal.component';
import { EditTextWithMetadataModalComponent } from '../modal/edit-text-with-metadata-modal/edit-text-with-metadata-modal.component';
import { SelectModifierModalComponent } from '../modal/select-modifier-modal/select-modifier-modal.component';
import { SelectSubactModalComponent } from '../modal/select-subact-modal/select-subact-modal.component';
import { InfoModalComponent } from '../modal/info-modal/info-modal.component';
import { AddSearchClauseModalComponent } from '../modal/add-search-clause-modal/add-search-clause-modal.component';

@Injectable({
  providedIn: 'root',
})
export class MyModalService {
  constructor(private modalService: BsModalService, private app: AppService) {}

  editCreationTask(
    task: CreationTaskDTO
  ): Observable<CreationTaskDTO | undefined> {
    const initialState = { creationTask: task };
    const modalRef = this.modalService.show(EditCreationTaskModalComponent, {
      initialState,
    });
    return modalRef.content?.creationTaskUpdate$
      ? modalRef.content.creationTaskUpdate$
      : of(undefined);
  }

  openConfirmationModal(title: string, message: string, options: string[]) {
    const initialState = { title, message, options };
    const modalRef = this.modalService.show(ConfirmationModalComponent, {
      initialState,
    });
    return modalRef.content?.confirmedWith$
      ? modalRef.content.confirmedWith$
      : of(undefined);
  }

  openInfoModal(title: string, message: string): void {
    const initialState = { title, message };
    this.modalService.show(InfoModalComponent, { initialState });
  }

  editModifier(modifier: ModifierDTO): Observable<ModifierDTO | undefined> {
    const initialState = { modifier: modifier };
    const modalRef = this.modalService.show(EditModifierModalComponent, {
      initialState,
      class: 'modal-lg',
    });
    return modalRef.content?.modifierUpdate$
      ? modalRef.content.modifierUpdate$
      : of(undefined);
  }

  editSubact(
    subact: SubactDTO,
    parents: SubactDTO[]
  ): Observable<SubactDTO | undefined> {
    const initialState = { subact: subact, parents: parents };
    const modalRef = this.modalService.show(EditSubactModalComponent, {
      initialState,
      class: 'modal-lg',
    });
    return modalRef.content?.subactUpdate$
      ? modalRef.content.subactUpdate$
      : of(undefined);
  }

  editText(
    text: TextWithMetadataDTO,
    creationTaskList: CreationTaskDTO[]
  ): Observable<TextWithMetadataDTO | undefined> {
    const initialState = { text: text, creationTaskList: creationTaskList };
    const modalRef = this.modalService.show(
      EditTextWithMetadataModalComponent,
      {
        initialState,
      }
    );
    return modalRef.content?.textUpdate$
      ? modalRef.content.textUpdate$
      : of(undefined);
  }

  selectModifiers(
    token: string,
    modifierList: ModifierDTO[],
    selectedModifiers: ModifierDTO[]
  ): Observable<ModifierDTO[]> {
    const initialState = { token, modifierList, selectedModifiers };
    const modalRef = this.modalService.show(SelectModifierModalComponent, {
      initialState,
      class: 'modal-lg',
    });
    return modalRef.content?.modifiersUpdate$
      ? modalRef.content.modifiersUpdate$
      : of([]);
  }

  selectSubacts(
    token: string,
    subactList: SubactDTO[],
    selectedSubacts: SubactDTO[]
  ): Observable<SubactDTO[]> {
    const initialState = { token, subactList, selectedSubacts };
    const modalRef = this.modalService.show(SelectSubactModalComponent, {
      initialState,
      class: 'modal-lg',
    });
    return modalRef.content?.subactsUpdate$
      ? modalRef.content.subactsUpdate$
      : of([]);
  }

  openAddSeearchClauseModal(
    clauses: SearchClause[]
  ): Observable<SearchClause[]> {
    const modalRef = this.modalService.show(AddSearchClauseModalComponent, {
      class: 'modal-lg',
      initialState: { clauses: clauses },
    });
    return modalRef.content?.clause$
      ? modalRef.content.clause$
      : of([...clauses]);
  }
}

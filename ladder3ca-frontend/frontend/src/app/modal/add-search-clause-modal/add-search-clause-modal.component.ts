import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { faTrash } from '@fortawesome/free-solid-svg-icons';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { Subject, take } from 'rxjs';
import {
  ClauseMode,
  CreationTaskDTO,
  LadderField,
  ModifierDTO,
  SearchClause,
  SubactDTO,
} from 'src/app/model';
import { AppService } from 'src/app/service/app.service';

@Component({
  selector: 'app-add-search-clause-modal',
  templateUrl: './add-search-clause-modal.component.html',
  styleUrls: ['./add-search-clause-modal.component.scss'],
})
export class AddSearchClauseModalComponent implements OnInit {
  faTrash = faTrash;
  @Input()
  clauses: SearchClause[] = [];

  clause$: Subject<SearchClause[]> = new Subject();

  textContentForm: FormGroup;

  clauseMode: ClauseMode = ClauseMode.AND;

  modifierList: ModifierDTO[] = [];
  subactList: SubactDTO[] = [];
  creationTaskList: CreationTaskDTO[] = [];

  selectedModifier: ModifierDTO | undefined = undefined;
  selectedSubact: SubactDTO | undefined = undefined;
  selectedCreationTask: CreationTaskDTO | undefined = undefined;

  constructor(
    fb: FormBuilder,
    private modalRef: BsModalRef,
    private app: AppService
  ) {
    this.textContentForm = fb.group({
      queryString: [],
    });
  }

  ngOnInit(): void {
    this.app.modifiers$.pipe(take(1)).subscribe((list) => {
      this.modifierList = list;
    });
    this.app.subacts$.pipe(take(1)).subscribe((list) => {
      this.subactList = list;
    });
    this.app.ct_listAll().subscribe((list) => {
      this.creationTaskList = list;
    });
  }

  close(): void {
    this.clause$.complete();
    this.modalRef.hide();
  }

  toggleClauseMode(): void {
    if (this.clauseMode === ClauseMode.AND) {
      this.clauseMode = ClauseMode.OR;
    } else {
      this.clauseMode = ClauseMode.AND;
    }
  }

  translateMode(mode: ClauseMode): string {
    return this.app.translateQueryMode(
      new SearchClause(mode, LadderField.TEXT_CONTENT, '')
    );
  }

  translateClauseMode(clause: SearchClause): string {
    return this.app.translateQueryMode(clause);
  }

  removeClause(index: number): void {
    this.clauses.splice(index, 1);
    this.clause$.next([...this.clauses]);
  }

  translateClause(clause: SearchClause): string {
    return this.app.translateQueryString(clause);
  }

  addClauseAndEmit(clause: SearchClause): void {
    this.clauses.push(clause);
    this.clause$.next([...this.clauses]);
  }

  addFulltextSearchClause(): void {
    const qs = this.textContentForm.get('queryString')?.value;
    if (!!qs) {
      const clause = new SearchClause(
        this.clauseMode,
        LadderField.TEXT_CONTENT,
        qs
      );
      this.addClauseAndEmit(clause);
      this.textContentForm.reset();
    }
  }

  addLanguageClause(lang: string): void {
    if (lang) {
      this.addClauseAndEmit(
        new SearchClause(this.clauseMode, LadderField.LANGUAGE, lang)
      );
    }
  }

  addSelectLanguageClause(event: any): void {
    const lang = event.target.value;
    if (lang) {
      this.addLanguageClause(lang);
    }
  }

  addGenderClause(gen: string): void {
    if (gen) {
      this.addClauseAndEmit(
        new SearchClause(this.clauseMode, LadderField.SPEAKER_GENDER, gen)
      );
    }
  }

  addSelectedGender(event: any): void {
    const gen = event.target.value;
    if (gen) {
      this.addGenderClause(gen);
    }
  }

  addModifierClause(): void {
    if (this.selectedModifier?.id) {
      this.addClauseAndEmit(
        new SearchClause(
          this.clauseMode,
          LadderField.MODIFIERS,
          this.selectedModifier.id.toString()
        )
      );
    }
  }

  addSelectedModifier(event: any): void {
    const mod = event.target.value;
    if (mod) {
      this.addModifierClause();
    }
  }

  addSubactClause(): void {
    if (this.selectedSubact?.id) {
      this.addClauseAndEmit(
        new SearchClause(
          this.clauseMode,
          LadderField.SUBACTS,
          this.selectedSubact.id.toString()
        )
      );
    }
  }

  addSelectedSubact(event: any): void {
    const sub = event.target.value;
    if (sub) {
      this.addSubactClause();
    }
  }

  addCreationTaskClause(): void {
    if (this.selectedCreationTask?.id) {
      this.addClauseAndEmit(
        new SearchClause(
          ClauseMode.OR,
          LadderField.CREATION_TASK,
          this.selectedCreationTask.id.toString()
        )
      );
    }
  }

  addSelectedCreationTask(event: any): void {
    const ct = event.target.value;
    if (ct) {
      this.addCreationTaskClause();
    }
  }
}

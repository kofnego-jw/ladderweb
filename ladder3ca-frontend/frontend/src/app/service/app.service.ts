import { Injectable } from '@angular/core';
import { RestService } from './rest.service';
import {
  AnnotatingRequestDTO,
  AnnotatingResultDTO,
  AppRole,
  AppUserDTO,
  AuditLogDTO,
  ClauseMode,
  CreationTaskDTO,
  LadderField,
  ModifierAnnotationDTO,
  ModifierDTO,
  OffCanvasAction,
  RetrainModelMessagesDTO,
  SearchClause,
  SearchResultDTO,
  SimpleMessageDTO,
  SpeakerGender,
  SubactAnnotationDTO,
  SubactDTO,
  TextDTO,
  TextWithMetadataDTO,
} from '../model';
import {
  Observable,
  ReplaySubject,
  Subject,
  catchError,
  map,
  of,
  timer,
} from 'rxjs';
import { BsModalService } from 'ngx-bootstrap/modal';
import { ErrorModalComponent } from '../modal/error-modal/error-modal.component';
import { InfoModalComponent } from '../modal/info-modal/info-modal.component';

@Injectable({
  providedIn: 'root',
})
export class AppService {
  modifiers: ModifierDTO[] = [];
  modifiers$: ReplaySubject<ModifierDTO[]> = new ReplaySubject(1);

  subacts: SubactDTO[] = [];
  subacts$: ReplaySubject<SubactDTO[]> = new ReplaySubject(1);

  creationTasks: CreationTaskDTO[] = [];
  creationTasks$: ReplaySubject<CreationTaskDTO[]> = new ReplaySubject(1);

  user$: ReplaySubject<AppUserDTO | undefined> = new ReplaySubject(1);
  user: AppUserDTO | undefined = undefined;

  keepAliveTimer: Observable<number> = timer(0, 60000);

  constructor(private rest: RestService, private modalService: BsModalService) {
    this.modifiers$.next(this.modifiers);
    this.subacts$.next(this.subacts);
    this.loadData();
    this.keepAliveTimer.subscribe(() => this.keepAlive());
  }

  // App methods
  handleError(err: any, message: string) {
    console.error(err);
    this.modalService.show(ErrorModalComponent, {
      initialState: { message: message },
    });
  }

  showMessage(msg: SimpleMessageDTO | undefined): void {
    if (msg) {
      this.modalService.show(InfoModalComponent, {
        initialState: { message: msg.message },
      });
    } else {
      this.modalService.show(InfoModalComponent, {
        initialState: {
          message: 'An error might have happend, there is no message back.',
        },
      });
    }
  }

  setAppUser(user: AppUserDTO | undefined): void {
    this.user = user;
    this.user$.next(this.user);
  }

  setModifiers(modifiers: ModifierDTO[]): void {
    this.modifiers = modifiers;
    this.modifiers$.next(this.modifiers);
  }

  setSubacts(subacts: SubactDTO[]): void {
    this.subacts = subacts;
    this.subacts$.next(this.subacts);
  }

  setCreationTasks(tasks: CreationTaskDTO[]): void {
    this.creationTasks = tasks;
    this.creationTasks$.next(this.creationTasks);
  }

  loadData(): void {
    this.loadModifiers();
    this.loadSubacts();
    this.loadCreationTasks();
  }

  translateQueryMode(clause: SearchClause): string {
    if (!clause) {
      return '';
    }
    switch (clause.mode) {
      case ClauseMode.AND:
        return 'MUST';
      case ClauseMode.OR:
        return 'SHOULD';
    }
    return '';
  }

  translateQueryString(clause: SearchClause): string {
    if (!clause) {
      return '';
    }
    if (clause.field === LadderField.MODIFIERS) {
      const mod = this.modifiers.find(
        (m) => m.id === Number.parseInt(clause.queryString)
      );
      return mod ? mod.modifierCode : clause.queryString;
    }
    if (clause.field === LadderField.SUBACTS) {
      const sub = this.subacts.find(
        (s) => s.id === Number.parseInt(clause.queryString)
      );
      return sub ? sub.fullName : clause.queryString;
    }
    if (clause.field === LadderField.CREATION_TASK) {
      const task = this.creationTasks.find(
        (t) => t.id === Number.parseInt(clause.queryString)
      );
      return task ? task.taskName : clause.queryString;
    }
    return clause.queryString;
  }

  // User methods
  login(username: string, password: string): void {
    this.rest
      .login(username, password)
      .pipe(
        catchError((err) => {
          this.handleError(err, 'Cannot login.');
          return of(undefined);
        })
      )
      .subscribe((user) => this.setAppUser(user));
  }

  logout(): void {
    this.rest
      .logout()
      .pipe(
        catchError((err) => {
          this.handleError(err, 'Cannot logout.');
          return of(undefined);
        })
      )
      .subscribe((user) => this.setAppUser(undefined));
  }

  keepAlive(): void {
    this.rest
      .activeUser()
      .pipe(
        catchError((err) => {
          this.handleError(err, 'Cannot logout.');
          return of(undefined);
        })
      )
      .subscribe((user) => {
        if (!this.user && user) {
          this.setAppUser(user);
        } else if (this.user && !user) {
          this.setAppUser(undefined);
        } else if (this.user && user && this.user.id !== user.id) {
          this.setAppUser(user);
        }
      });
  }

  user_changeOwnPassword(oldPassword: string, newPassword: string): void {
    if (!this.user) {
      return;
    }
    this.rest
      .changePassword(this.user.id, oldPassword, newPassword)
      .pipe(
        catchError((err) => {
          this.handleError(err, 'Cannot change password.');
          return of(undefined);
        })
      )
      .subscribe((msg) => this.showMessage(msg));
  }

  user_listOwnLogs(): Observable<AuditLogDTO[]> {
    if (!this.user) {
      return of([]);
    }
    return this.rest.getLogsByUser().pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot load logs.');
        return of([]);
      })
    );
  }

  loadModifiers(): void {
    this.rest
      .listAllModifiers()
      .pipe(
        catchError((err) => {
          this.handleError(err, 'Cannot load modifiers.');
          return [];
        })
      )
      .subscribe((modifiers) => this.setModifiers(modifiers));
  }

  loadSubacts(): void {
    this.rest
      .listAllSubacts()
      .pipe(
        catchError((err) => {
          this.handleError(err, 'Cannot load subacts.');
          return [];
        })
      )
      .subscribe((subacts) => this.setSubacts(subacts));
  }

  loadCreationTasks(): void {
    this.rest
      .listAllCreationTasks()
      .pipe(
        catchError((err) => {
          this.handleError(err, 'Cannot load creation tasks.');
          return [];
        })
      )
      .subscribe((tasks) => this.setCreationTasks(tasks));
  }

  annotate(
    text: string,
    useMax: boolean,
    language: string,
    modifiers: ModifierDTO[],
    subacts: SubactDTO[]
  ): Observable<AnnotatingResultDTO | undefined> {
    const modifierIds = modifiers
      .map((m) => m.id)
      .filter((id): id is number => !!id);
    const subactIds = subacts
      .map((s) => s.id)
      .filter((id): id is number => !!id);
    const request = new AnnotatingRequestDTO(
      text,
      modifierIds,
      subactIds,
      language,
      useMax
    );
    return this.rest.annotateUseModel(request).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot annotate.');
        return of(undefined);
      })
    );
  }

  // Data methods

  ct_listAll(): Observable<CreationTaskDTO[]> {
    return this.rest.listAllCreationTasks().pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot load creation tasks.');
        return of([]);
      })
    );
  }

  ct_addNew(
    taskName: string,
    desc: string,
    category: string
  ): Observable<CreationTaskDTO | undefined> {
    const dto = new CreationTaskDTO(0, taskName, desc, category);
    return this.rest.createCreationTask(dto).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot add new creation task.');
        return of(undefined);
      })
    );
  }

  ct_update(
    id: number,
    taskName: string,
    desc: string,
    category: string
  ): Observable<CreationTaskDTO | undefined> {
    const dto = new CreationTaskDTO(id, taskName, desc, category);
    return this.rest.updateCreationTask(id, dto).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot update creation task.');
        return of(undefined);
      })
    );
  }

  ct_delete(id: number): Observable<CreationTaskDTO[]> {
    return this.rest.deleteCreationTask(id).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot delete creation task.');
        return of([]);
      })
    );
  }

  mod_listAll(): Observable<ModifierDTO[]> {
    return this.rest.listAllModifiers().pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot load modifiers.');
        return of([]);
      })
    );
  }

  mod_addNew(
    modifierCode: string,
    desc: string
  ): Observable<ModifierDTO | undefined> {
    const dto = new ModifierDTO(undefined, modifierCode, desc);
    return this.rest.addNewModifier(dto).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot add new modifier.');
        return of(undefined);
      })
    );
  }

  mod_update(
    id: number,
    modifierCode: string,
    desc: string
  ): Observable<ModifierDTO | undefined> {
    const dto = new ModifierDTO(id, modifierCode, desc);
    return this.rest.updateOneModifier(id, dto).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot update modifier.');
        return of(undefined);
      })
    );
  }

  mod_delete(id: number): Observable<ModifierDTO[]> {
    return this.rest.deleteModifier(id).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot delete modifier.');
        return of([]);
      })
    );
  }

  sub_listAll(): Observable<SubactDTO[]> {
    return this.rest.listAllSubacts().pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot load subacts.');
        return of([]);
      })
    );
  }

  sub_addNew(
    subactName: string,
    desc: string,
    parentId: number | undefined
  ): Observable<SubactDTO | undefined> {
    const dto = new SubactDTO(undefined, subactName, parentId, undefined, desc);
    return this.rest.addNewSubact(dto).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot add new subact.');
        return of(undefined);
      })
    );
  }

  sub_update(
    id: number,
    modifierCode: string,
    desc: string,
    parentId: number | undefined
  ): Observable<SubactDTO | undefined> {
    const dto = new SubactDTO(id, modifierCode, parentId, undefined, desc);
    return this.rest.updateOneSubact(id, dto).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot update subact.');
        return of(undefined);
      })
    );
  }

  sub_delete(id: number): Observable<SubactDTO[]> {
    return this.rest.deleteSubact(id).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot delete subact.');
        return of([]);
      })
    );
  }

  txt_listAll(): Observable<TextDTO[]> {
    return this.rest.listAllTexts().pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot load texts.');
        return of([]);
      })
    );
  }

  txt_listLatest(count: number): Observable<TextDTO[]> {
    return this.rest.listLatestTexts(count).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot load latest texts.');
        return of([]);
      })
    );
  }

  txt_readOne(id: string): Observable<TextWithMetadataDTO | undefined> {
    return this.rest.getOneText(id).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot load text with metadata.');
        return of(undefined);
      })
    );
  }

  txt_addNew(
    altId: string,
    textdata: string,
    languageCode: string,
    creationTask: CreationTaskDTO | undefined,
    gender: SpeakerGender | undefined,
    ageAtCreation: number,
    l1Language: string,
    l2Languages: string,
    location: string
  ): Observable<TextWithMetadataDTO | undefined> {
    const dto = new TextWithMetadataDTO(
      undefined,
      altId,
      textdata,
      languageCode,
      creationTask,
      0,
      gender,
      ageAtCreation,
      l1Language,
      l2Languages,
      location,
      []
    );
    return this.rest.addNewText(dto).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot add new text.');
        return of(undefined);
      })
    );
  }

  txt_update(
    id: string,
    altId: string,
    textdata: string,
    languageCode: string,
    creationTask: CreationTaskDTO | undefined,
    gender: SpeakerGender | undefined,
    ageAtCreation: number,
    l1Language: string,
    l2Languages: string,
    location: string
  ): Observable<TextWithMetadataDTO | undefined> {
    const dto = new TextWithMetadataDTO(
      id,
      altId,
      textdata,
      languageCode,
      creationTask,
      0,
      gender,
      ageAtCreation,
      l1Language,
      l2Languages,
      location,
      []
    );
    return this.rest.updateOneText(id, dto).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot update text.');
        return of(undefined);
      })
    );
  }

  txt_delete(id: string): Observable<TextDTO[]> {
    return this.rest.deleteText(id).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot delete text.');
        return of([]);
      })
    );
  }

  ann_listModifiersByText(id: string): Observable<ModifierAnnotationDTO[]> {
    return this.rest.getModifierAnnotationsByText(id).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot load modifier annotations.');
        return of([]);
      })
    );
  }

  ann_listSubactsByText(id: string): Observable<SubactAnnotationDTO[]> {
    return this.rest.getSubactAnnotationsByText(id).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot load subact annotations.');
        return of([]);
      })
    );
  }

  ann_saveModifierAnnotations(
    id: string,
    annotations: ModifierAnnotationDTO[]
  ): Observable<ModifierAnnotationDTO[]> {
    return this.rest.updateModifierAnnotationsByText(id, annotations).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot save modifier annotations.');
        return of([]);
      })
    );
  }

  ann_saveSubactAnnotations(
    id: string,
    annotations: SubactAnnotationDTO[]
  ): Observable<SubactAnnotationDTO[]> {
    return this.rest.updateSubactAnnotationsByText(id, annotations).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot save subact annotations.');
        return of([]);
      })
    );
  }

  pos_trainAll(): Observable<SimpleMessageDTO | undefined> {
    return this.rest.retrainAllModels().pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot train all models.');
        return of(undefined);
      })
    );
  }

  pos_trainIsRunning(): Observable<boolean> {
    return this.rest.retrainTrainingRunning().pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot check if training is running.');
        return of(
          new SimpleMessageDTO(500, 'Cannot check if training is running.', [])
        );
      }),
      map((msg) => msg?.message === 'Training')
    );
  }

  pos_trainingMessages(): Observable<RetrainModelMessagesDTO | undefined> {
    return this.rest.retrainModelMessages().pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot load training messages.');
        return of(undefined);
      })
    );
  }

  pos_trainModifier(
    modifier: ModifierDTO,
    language: string
  ): Observable<SimpleMessageDTO | undefined> {
    if (modifier.id !== undefined) {
      return this.rest
        .retrainModifierModelByLanguage(modifier.id, language)
        .pipe(
          catchError((err) => {
            this.handleError(err, 'Cannot train modifier.');
            return of(undefined);
          })
        );
    } else {
      return of(
        new SimpleMessageDTO(
          500,
          'Cannot train modifier, no modifier id found.',
          []
        )
      );
    }
  }

  pos_trainSubact(
    subact: SubactDTO,
    language: string
  ): Observable<SimpleMessageDTO | undefined> {
    if (subact.id !== undefined) {
      return this.rest.retrainSubactModelByLanguage(subact.id, language).pipe(
        catchError((err) => {
          this.handleError(err, 'Cannot train subact.');
          return of(undefined);
        })
      );
    } else {
      return of(
        new SimpleMessageDTO(
          500,
          'Cannot train subact, no subact id found.',
          []
        )
      );
    }
  }

  search(clauses: SearchClause[]): Observable<SearchResultDTO | undefined> {
    if (!clauses || !clauses.length) {
      return of(undefined);
    }
    return this.rest.search(clauses).pipe(
      catchError((err) => {
        this.handleError(err, 'Cannot search.');
        return of(undefined);
      })
    );
  }

  corpus_download(): void {
    window.open('/api/v1/corpus/download', '_blank');
  }

  corpus_downloadCsv(): void {
    window.open('/api/v1/corpus/download/csv', '_blank');
  }

  corpus_downloadTei(): void {
    window.open('/api/v1/corpus/download/xmltei', '_blank');
  }

  corpus_downloadSearch(clauses: SearchClause[]): void {
    if (!clauses || !clauses.length) {
      return;
    }
    this.rest.searchAndDownload(clauses);
  }

  corpus_downloadSearchCsv(clauses: SearchClause[]): void {
    if (!clauses || !clauses.length) {
      return;
    }
    this.rest.searchAndDownloadCsv(clauses);
  }

  corpus_downloadSearchTei(clauses: SearchClause[]): void {
    if (!clauses || !clauses.length) {
      return;
    }
    this.rest.searchAndDownloadTei(clauses);
  }

  // Admin methods

  adm_listAllUsers(): Observable<AppUserDTO[]> {
    if (this.user?.roles.includes(AppRole.ADMIN)) {
      return this.rest.adm_listAllUsers().pipe(
        catchError((err) => {
          this.handleError(err, 'Cannot load users.');
          return of([]);
        })
      );
    }
    return of([]);
  }

  adm_createUser(
    email: string,
    password: string,
    roles: AppRole[]
  ): Observable<SimpleMessageDTO | undefined> {
    if (this.user?.roles.includes(AppRole.ADMIN)) {
      return this.rest.adm_createUser(email, password, roles).pipe(
        catchError((err) => {
          this.handleError(err, 'Cannot create user.');
          return of(undefined);
        })
      );
    }
    return of(
      new SimpleMessageDTO(500, 'Cannot create user, you are not an admin.', [])
    );
  }

  adm_changePassword(
    userId: number,
    newPassword: string
  ): Observable<SimpleMessageDTO | undefined> {
    if (this.user?.roles.includes(AppRole.ADMIN)) {
      return this.rest.adm_changePassword(userId, newPassword).pipe(
        catchError((err) => {
          this.handleError(err, 'Cannot change password.');
          return of(undefined);
        })
      );
    }
    return of(
      new SimpleMessageDTO(
        500,
        'Cannot change password, you are not an admin.',
        []
      )
    );
  }

  adm_updateUser(
    id: number,
    email: string,
    roles: AppRole[]
  ): Observable<SimpleMessageDTO | undefined> {
    if (this.user?.roles.includes(AppRole.ADMIN)) {
      return this.rest.adm_updateUser(id, email, roles).pipe(
        catchError((err) => {
          this.handleError(err, 'Cannot update user.');
          return of(undefined);
        })
      );
    }
    return of(
      new SimpleMessageDTO(500, 'Cannot update user, you are not an admin.', [])
    );
  }

  adm_getLogs(): Observable<AuditLogDTO[]> {
    if (this.user?.roles.includes(AppRole.ADMIN)) {
      return this.rest.adm_getLogs().pipe(
        catchError((err) => {
          this.handleError(err, 'Cannot load logs.');
          return of([]);
        })
      );
    }
    return of([]);
  }
}

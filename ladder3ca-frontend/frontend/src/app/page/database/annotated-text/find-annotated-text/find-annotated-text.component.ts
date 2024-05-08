import { Component, OnInit } from '@angular/core';
import {
  faCheck,
  faPencil,
  faTrash,
  faStar,
  faTag,
} from '@fortawesome/free-solid-svg-icons';
import { PageChangedEvent } from 'ngx-bootstrap/pagination';
import { take } from 'rxjs';
import {
  CreationTaskDTO,
  LadderField,
  SearchClause,
  SearchHit,
  SearchResultDTO,
  TextDTO,
  TextWithMetadataDTO,
} from 'src/app/model';
import { AppService } from 'src/app/service/app.service';
import { EditTextStateService } from 'src/app/service/edit-text-state.service';
import { MyFilter } from 'src/app/service/filter.service';
import { MyModalService } from 'src/app/service/my-modal.service';
import {
  MyPagingService,
  PagingStatus,
} from 'src/app/service/my-paging.service';
import {
  MySortingService,
  SortStatus,
} from 'src/app/service/my-sorting.service';

@Component({
  selector: 'app-find-annotated-text',
  templateUrl: './find-annotated-text.component.html',
  styleUrls: ['./find-annotated-text.component.scss'],
})
export class FindAnnotatedTextComponent implements OnInit {
  faCheck = faCheck;
  faPencil = faPencil;
  faTrash = faTrash;
  faStar = faStar;
  faTag = faTag;

  fields: LadderField[] = [
    LadderField.TEXT_CONTENT,
    LadderField.LANGUAGE,
    LadderField.L1_LANGUAGE,
    LadderField.L2_LANGUAGE,
  ];

  clauses: SearchClause[] = [];

  creationTaskList: CreationTaskDTO[] = [];

  result: SearchResultDTO | undefined = undefined;

  pagingStatus = new PagingStatus(20, 0, 1);

  shownHits: SearchHit[] = [];

  filteredHits: SearchHit[] = [];

  myFilter: MyFilter = MyFilter.NO_FILTER;

  sortStatus: SortStatus = new SortStatus(undefined, true);

  constructor(
    private app: AppService,
    private modal: MyModalService,
    private sorting: MySortingService,
    private paging: MyPagingService,
    private editText: EditTextStateService
  ) {}

  ngOnInit(): void {
    this.app.ct_listAll().subscribe((list) => {
      this.creationTaskList = list;
    });
  }

  translateClause(clause: SearchClause): string {
    return this.app.translateQueryString(clause);
  }

  translateMode(clause: SearchClause): string {
    return this.app.translateQueryMode(clause);
  }

  doSearch(): void {
    this.app.search(this.clauses).subscribe((result) => {
      this.result = result;
      if (this.result) {
        this.pagingStatus = this.pagingStatus.setTotalNumber(
          this.result?.totalHits
        );
      } else {
        this.pagingStatus = this.pagingStatus.setTotalNumber(0);
      }
      this.filterAndSort();
    });
  }

  filterAndSort(): void {
    const hits = this.result ? this.result.hits : [];
    let filtered = this.myFilter.filterSearchHit(hits);
    this.filteredHits = this.sorting.sortBy(filtered, this.sortStatus);
    this.shownHits = this.paging.paginate(this.filteredHits, this.pagingStatus);
  }

  sortBy(field: string): void {
    this.sortStatus = this.sortStatus.setFieldname(field);
    this.filterAndSort();
  }

  setMyFilter(f: MyFilter): void {
    this.myFilter = f;
    this.filterAndSort();
  }

  onPageChanged(event: PageChangedEvent): void {
    if (event) {
      this.pagingStatus = this.pagingStatus.setPageNumber(event.page);
      this.filterAndSort();
    }
  }

  getCreationTaskName(text: TextWithMetadataDTO | undefined): string {
    if (!text || !text.creationTask) {
      return 'none';
    }
    const task = this.creationTaskList.find(
      (x) => x.id === text.creationTask?.id
    );
    return task ? task.taskName : 'none';
  }

  downloadSearchResult(): void {
    this.app.corpus_downloadSearch(this.clauses);
  }

  downloadSearchResultAsCsv(): void {
    this.app.corpus_downloadSearchCsv(this.clauses);
  }

  downloadSearchResultAsTei(): void {
    this.app.corpus_downloadSearchTei(this.clauses);
  }

  removeClause(clause: SearchClause): void {
    this.clauses = this.clauses.filter((c) => c !== clause);
    if (this.clauses.length === 0) {
      this.result = undefined;
    } else {
      this.doSearch();
    }
  }

  addClause(): void {
    this.modal.openAddSeearchClauseModal(this.clauses).subscribe({
      next: (c) => {
        if (c.length > 0) {
          this.clauses = c;
        }
      },
      complete: () => this.doSearch(),
    });
  }

  downloadAll(): void {
    this.app.corpus_download();
  }

  downloadAllAsCsv(): void {
    this.app.corpus_downloadCsv();
  }

  downloadAllAsTei(): void {
    this.app.corpus_downloadTei();
  }

  openEditTextModal(text: TextWithMetadataDTO): void {
    this.modal
      .editText(text, this.creationTaskList)
      .pipe(take(1))
      .subscribe((updated) => {
        if (updated) {
          if (updated.id !== undefined) {
            this.app
              .txt_update(
                updated.id,
                updated.altId,
                updated.textdata,
                updated.languageCode,
                updated.creationTask,
                updated.gender,
                updated.ageAtCreation,
                updated.l1Language,
                updated.l2Languages,
                updated.location
              )
              .subscribe((dto) => this.doSearch());
          } else {
            this.app
              .txt_addNew(
                updated.altId,
                updated.textdata,
                updated.languageCode,
                updated.creationTask,
                updated.gender,
                updated.ageAtCreation,
                updated.l1Language,
                updated.l2Languages,
                updated.location
              )
              .subscribe((dto) => this.doSearch());
          }
        }
      });
  }

  updateText(text: TextDTO | undefined): void {
    if (!text) {
      return;
    }
    if (text.id) {
      this.app.txt_readOne(text.id).subscribe((dto) => {
        if (dto) {
          this.openEditTextModal(dto);
        }
      });
    } else {
      this.openEditTextModal(
        new TextWithMetadataDTO(
          undefined,
          '',
          '',
          '',
          undefined,
          0,
          undefined,
          0,
          '',
          '',
          '',
          []
        )
      );
    }
  }

  deleteText(text: TextDTO | undefined): void {
    if (!text || !text.id) {
      return;
    }
    this.modal
      .openConfirmationModal(
        'Delete a Text',
        'Do you really want to remove this text from the database?',
        ['delete', 'cancel']
      )
      .subscribe((opt) => {
        if (opt && opt === 'delete' && text.id) {
          this.app.txt_delete(text.id).subscribe((list) => this.doSearch());
        }
      });
  }

  annotateText(text: TextDTO | undefined): void {
    if (!text) {
      return;
    }
    this.editText.annotateText(text);
  }
}

import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  faCheck,
  faPencil,
  faStar,
  faTag,
  faTrash,
} from '@fortawesome/free-solid-svg-icons';
import { PageChangedEvent } from 'ngx-bootstrap/pagination';
import { Subject, take, takeUntil } from 'rxjs';
import { CreationTaskDTO, TextDTO, TextWithMetadataDTO } from 'src/app/model';
import { AppService } from 'src/app/service/app.service';
import { EditTextStateService } from 'src/app/service/edit-text-state.service';
import { FilterService, MyFilter } from 'src/app/service/filter.service';
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
  selector: 'app-annotated-text-list',
  templateUrl: './annotated-text-list.component.html',
  styleUrls: ['./annotated-text-list.component.scss'],
})
export class AnnotatedTextListComponent implements OnInit, OnDestroy {
  destroyed$: Subject<boolean> = new Subject();
  faCheck = faCheck;
  faPencil = faPencil;
  faTrash = faTrash;
  faStar = faStar;
  faTag = faTag;

  textList: TextDTO[] = [];
  filteredTextList: TextDTO[] = [];
  shownTextList: TextDTO[] = [];

  creationTaskList: CreationTaskDTO[] = [];

  pagingStatus: PagingStatus = new PagingStatus(10, 0, 1);

  myFilter: MyFilter = MyFilter.NO_FILTER;

  sortStatus: SortStatus = new SortStatus(undefined, true);

  constructor(
    private app: AppService,
    private sorting: MySortingService,
    private paging: MyPagingService,
    private filter: FilterService,
    private editText: EditTextStateService,
    private modal: MyModalService
  ) {}

  onChange(): void {
    this.loadTextList();
  }

  setTextList(list: TextDTO[]): void {
    this.textList = list;
    this.pagingStatus = this.pagingStatus.setTotalNumber(this.textList.length);
    this.filterAndSort();
  }

  filterAndSort(): void {
    let filtered = this.myFilter.filterTextList(this.textList);
    this.filteredTextList = this.sorting.sortBy(filtered, this.sortStatus);
    this.shownTextList = this.paging.paginate(
      this.filteredTextList,
      this.pagingStatus
    );
  }

  onPageChanged(event: PageChangedEvent): void {
    if (event) {
      this.pagingStatus = this.pagingStatus.setPageNumber(event.page);
      this.filterAndSort();
    }
  }

  sortBy(field: string): void {
    this.sortStatus = this.sortStatus.setFieldname(field);
    this.filterAndSort();
  }

  setMyFilter(f: MyFilter): void {
    this.myFilter = f;
    this.filterAndSort();
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
              .subscribe((dto) => this.loadTextList());
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
              .subscribe((dto) => this.loadTextList());
          }
        }
      });
  }

  updateText(text: TextDTO): void {
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

  deleteText(text: TextDTO): void {
    if (!text.id) {
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
          this.app.txt_delete(text.id).subscribe((list) => this.loadTextList());
        }
      });
  }

  createNewText(): void {
    const newText = new TextDTO(undefined, '', '', '', undefined, 0);
    this.updateText(newText);
  }

  loadTextList(): void {
    this.app.txt_listLatest(20).subscribe((list) => {
      this.setTextList(list);
      this.setMyFilter(this.myFilter);
      this.filterAndSort();
    });
  }

  ngOnInit(): void {
    this.app
      .ct_listAll()
      .pipe(take(1))
      .subscribe((list) => (this.creationTaskList = list));
    this.loadTextList();
    this.filter.filter$
      .pipe(takeUntil(this.destroyed$))
      .subscribe((f) => this.setMyFilter(f));
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  annotateText(text: TextDTO): void {
    if (!!text) {
      this.editText.annotateText(text);
    }
  }

  getCreationTaskName(text: TextDTO): string {
    if (!text.creationTask) {
      return 'none';
    }
    const task = this.creationTaskList.find(
      (x) => x.id === text.creationTask?.id
    );
    return task ? task.taskName : 'none';
  }
}

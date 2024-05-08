import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  faCheck,
  faPencil,
  faStar,
  faTrash,
} from '@fortawesome/free-solid-svg-icons';
import { PageChangedEvent } from 'ngx-bootstrap/pagination';
import { Subject, take, takeUntil } from 'rxjs';
import { CreationTaskDTO } from 'src/app/model';
import { AppService } from 'src/app/service/app.service';
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
  selector: 'app-creation-task-list',
  templateUrl: './creation-task-list.component.html',
  styleUrls: ['./creation-task-list.component.scss'],
})
export class CreationTaskListComponent implements OnInit, OnDestroy {
  destroyed$: Subject<boolean> = new Subject();
  faCheck = faCheck;
  faPencil = faPencil;
  faTrash = faTrash;
  faStar = faStar;

  creationTaskList: CreationTaskDTO[] = [];
  filteredCreationTaskList: CreationTaskDTO[] = [];
  shownCreationTaskList: CreationTaskDTO[] = [];

  pagingStatus: PagingStatus = new PagingStatus(10, 0, 1);

  myFilter: MyFilter = MyFilter.NO_FILTER;

  sortStatus: SortStatus = new SortStatus(undefined, true);

  constructor(
    private app: AppService,
    private sorting: MySortingService,
    private paging: MyPagingService,
    private filter: FilterService,
    private modal: MyModalService
  ) {}

  onChange(): void {
    this.loadCreationTaskList();
  }

  setCreationTaskList(list: CreationTaskDTO[]): void {
    this.creationTaskList = list;
    this.pagingStatus = this.pagingStatus.setTotalNumber(
      this.creationTaskList.length
    );
    this.filterAndSort();
  }

  filterAndSort(): void {
    let filtered = this.myFilter.filterCreationTasks(this.creationTaskList);
    this.filteredCreationTaskList = this.sorting.sortBy(
      filtered,
      this.sortStatus
    );
    this.shownCreationTaskList = this.paging.paginate(
      this.filteredCreationTaskList,
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

  updateCreationTask(task: CreationTaskDTO): void {
    this.modal
      .editCreationTask(task)
      .pipe(take(1))
      .subscribe((updated) => {
        if (updated) {
          if (updated.id !== undefined) {
            this.app
              .ct_update(
                updated.id,
                updated.taskName,
                updated.desc,
                updated.category
              )
              .subscribe((dto) => this.loadCreationTaskList());
          } else {
            this.app
              .ct_addNew(updated.taskName, updated.desc, updated.category)
              .subscribe((dto) => this.loadCreationTaskList());
          }
        }
      });
  }

  deleteCreationTask(creationTask: CreationTaskDTO): void {
    if (!creationTask.id) {
      return;
    }
    this.modal
      .openConfirmationModal(
        'Delete a Creation Task',
        'Do you really want to remove this creation task from the database?',
        ['delete', 'cancel']
      )
      .subscribe((opt) => {
        if (opt && opt === 'delete' && creationTask.id) {
          this.app
            .ct_delete(creationTask.id)
            .subscribe((list) => this.loadCreationTaskList());
        }
      });
  }

  createNewCreationTask(): void {
    const newTask = new CreationTaskDTO(undefined, '', '', '');
    this.updateCreationTask(newTask);
  }

  loadCreationTaskList(): void {
    this.app.ct_listAll().subscribe((list) => {
      this.setCreationTaskList(list);
      this.setMyFilter(this.myFilter);
      this.filterAndSort();
    });
  }

  ngOnInit(): void {
    this.loadCreationTaskList();
    this.filter.filter$
      .pipe(takeUntil(this.destroyed$))
      .subscribe((f) => this.setMyFilter(f));
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }
}

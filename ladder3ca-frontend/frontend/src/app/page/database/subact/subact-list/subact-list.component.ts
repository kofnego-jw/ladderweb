import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  faCheck,
  faPencil,
  faStar,
  faTrash,
} from '@fortawesome/free-solid-svg-icons';
import { PageChangedEvent } from 'ngx-bootstrap/pagination';
import { Subject, take, takeUntil } from 'rxjs';
import { SubactDTO } from 'src/app/model';
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
  selector: 'app-subact-list',
  templateUrl: './subact-list.component.html',
  styleUrls: ['./subact-list.component.scss'],
})
export class SubactListComponent implements OnInit, OnDestroy {
  destroyed$: Subject<boolean> = new Subject();
  faCheck = faCheck;
  faPencil = faPencil;
  faTrash = faTrash;
  faStar = faStar;

  subactList: SubactDTO[] = [];
  filteredSubactList: SubactDTO[] = [];
  shownSubactList: SubactDTO[] = [];

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
    this.loadSubactList();
  }

  setSubactList(list: SubactDTO[]): void {
    this.subactList = list;
    this.pagingStatus = this.pagingStatus.setTotalNumber(
      this.subactList.length
    );
    this.filterAndSort();
  }

  filterAndSort(): void {
    let filtered = this.myFilter.filterSubactList(this.subactList);
    this.filteredSubactList = this.sorting.sortBy(filtered, this.sortStatus);
    this.shownSubactList = this.paging.paginate(
      this.filteredSubactList,
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

  updateSubact(subact: SubactDTO): void {
    const parents = this.subactList.filter(
      (s) => !s.parentSubactId && s.id !== subact.id
    );
    this.modal
      .editSubact(subact, parents)
      .pipe(take(1))
      .subscribe((updated) => {
        if (updated) {
          if (updated.id !== undefined) {
            this.app
              .sub_update(
                updated.id,
                updated.subactName,
                updated.desc,
                updated.parentSubactId
              )
              .subscribe((dto) => this.loadSubactList());
          } else {
            this.app
              .sub_addNew(
                updated.subactName,
                updated.desc,
                updated.parentSubactId
              )
              .subscribe((dto) => this.loadSubactList());
          }
        }
      });
  }

  deleteSubact(subact: SubactDTO): void {
    if (!subact.id) {
      return;
    }
    this.modal
      .openConfirmationModal(
        'Delete a Subact Tag',
        'Do you really want to remove this subact tag from the database?',
        ['delete', 'cancel']
      )
      .subscribe((opt) => {
        if (opt && opt === 'delete' && subact.id) {
          this.app
            .sub_delete(subact.id)
            .subscribe((list) => this.loadSubactList());
        }
      });
  }

  createNewSubact(): void {
    const newSubact = new SubactDTO(undefined, '', undefined, undefined, '');
    this.updateSubact(newSubact);
  }

  loadSubactList(): void {
    this.app.sub_listAll().subscribe((list) => {
      this.setSubactList(list);
      this.setMyFilter(this.myFilter);
      this.filterAndSort();
    });
  }

  ngOnInit(): void {
    this.loadSubactList();
    this.filter.filter$
      .pipe(takeUntil(this.destroyed$))
      .subscribe((f) => this.setMyFilter(f));
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }
}

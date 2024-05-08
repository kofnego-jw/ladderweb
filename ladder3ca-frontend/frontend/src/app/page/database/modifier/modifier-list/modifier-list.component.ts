import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  faCheck,
  faPencil,
  faStar,
  faTrash,
} from '@fortawesome/free-solid-svg-icons';
import { PageChangedEvent } from 'ngx-bootstrap/pagination';
import { Subject, take, takeUntil } from 'rxjs';
import { ModifierDTO } from 'src/app/model';
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
  selector: 'app-modifier-list',
  templateUrl: './modifier-list.component.html',
  styleUrls: ['./modifier-list.component.scss'],
})
export class ModifierListComponent implements OnInit, OnDestroy {
  destroyed$: Subject<boolean> = new Subject();
  faCheck = faCheck;
  faPencil = faPencil;
  faTrash = faTrash;
  faStar = faStar;

  modifierList: ModifierDTO[] = [];
  filteredModifierList: ModifierDTO[] = [];
  shownModifierList: ModifierDTO[] = [];

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
    this.loadModifierList();
  }

  setModifierList(list: ModifierDTO[]): void {
    this.modifierList = list;
    this.pagingStatus = this.pagingStatus.setTotalNumber(
      this.modifierList.length
    );
    this.filterAndSort();
  }

  filterAndSort(): void {
    let filtered = this.myFilter.filterModifierList(this.modifierList);
    this.filteredModifierList = this.sorting.sortBy(filtered, this.sortStatus);
    this.shownModifierList = this.paging.paginate(
      this.filteredModifierList,
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

  updateModifier(modifier: ModifierDTO): void {
    this.modal
      .editModifier(modifier)
      .pipe(take(1))
      .subscribe((updated) => {
        if (updated) {
          if (updated.id !== undefined) {
            this.app
              .mod_update(updated.id, updated.modifierCode, updated.desc)
              .subscribe((dto) => this.loadModifierList());
          } else {
            this.app
              .mod_addNew(updated.modifierCode, updated.desc)
              .subscribe((dto) => this.loadModifierList());
          }
        }
      });
  }

  deleteModifier(modifier: ModifierDTO): void {
    if (!modifier.id) {
      return;
    }
    this.modal
      .openConfirmationModal(
        'Delete a Modifier Tag',
        'Do you really want to remove this modifier tag from the database?',
        ['delete', 'cancel']
      )
      .subscribe((opt) => {
        if (opt && opt === 'delete' && modifier.id) {
          this.app
            .mod_delete(modifier.id)
            .subscribe((list) => this.loadModifierList());
        }
      });
  }

  createNewModifier(): void {
    const newModifier = new ModifierDTO(undefined, '', '');
    this.updateModifier(newModifier);
  }

  loadModifierList(): void {
    this.app.mod_listAll().subscribe((list) => {
      this.setModifierList(list);
      this.setMyFilter(this.myFilter);
      this.filterAndSort();
    });
  }

  ngOnInit(): void {
    this.loadModifierList();
    this.filter.filter$
      .pipe(takeUntil(this.destroyed$))
      .subscribe((f) => this.setMyFilter(f));
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }
}

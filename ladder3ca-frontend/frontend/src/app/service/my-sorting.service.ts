import { Injectable } from '@angular/core';
import { ReplaySubject, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class MySortingService {
  currentSortStatus = new SortStatus(undefined, true);

  sortStatus$: Subject<SortStatus> = new ReplaySubject(1);

  constructor() {}

  setSortField(fieldname: string): void {
    this.currentSortStatus = this.currentSortStatus.setFieldname(fieldname);
    this.sortStatus$.next(this.currentSortStatus);
  }

  resetSortField(): void {
    this.currentSortStatus = new SortStatus(undefined, true);
    this.sortStatus$.next(this.currentSortStatus);
  }

  createSortFunction(sortStatus: SortStatus): (a: any, b: any) => number {
    return (a, b) => {
      if (!sortStatus.fieldname) {
        return 0;
      }
      const aElement = a.textMetadata ? a.textMetadata : a;
      const bElement = b.textMetadata ? b.textMetadata : b;
      let e1, e2;
      if (sortStatus.fieldname === 'creationTask.id') {
        e1 = a.creationTask ? a.creationTask.id : undefined;
        e2 = b.creationTask ? b.creationTask.id : undefined;
      } else {
        e1 = aElement[sortStatus.fieldname];
        e2 = bElement[sortStatus.fieldname];
      }
      let ascValue = 0;
      if (e1 === undefined) {
        ascValue = e2 === undefined ? 0 : 1;
      } else if (e2 === undefined) {
        ascValue = -1;
      } else {
        ascValue = e1 === e2 ? 0 : e1 < e2 ? -1 : 1;
      }
      if (sortStatus.asc) {
        return ascValue;
      }
      return -ascValue;
    };
  }

  sortBy(inputList: any[], sortStatus: SortStatus): any[] {
    if (!inputList) {
      return [];
    }
    if (!sortStatus || !sortStatus.fieldname) {
      return inputList.slice();
    }
    return inputList.slice().sort(this.createSortFunction(sortStatus));
  }
}

export class SortStatus {
  constructor(public fieldname: string | undefined, public asc: boolean) {}
  setFieldname(fieldname: string): SortStatus {
    if (this.fieldname === fieldname) {
      if (this.asc) {
        return new SortStatus(fieldname, !this.asc);
      }
      return new SortStatus(undefined, true);
    }
    return new SortStatus(fieldname, true);
  }
}

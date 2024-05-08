import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class MyPagingService {
  constructor() {}

  paginate(aList: any[], currentPaging: PagingStatus | undefined): any[] {
    if (!currentPaging) {
      return [];
    }
    if (!aList) {
      return [];
    }
    const start = currentPaging.startNumber();
    if (aList.length > start) {
      const end = currentPaging.endNumber();
      return aList.slice(start, end);
    }
    return [];
  }
}

/**
 * PagingStatus is 1-Based, the page number starts with 1.
 */
export class PagingStatus {
  readonly totalPages: number;
  readonly currentPage: number;
  constructor(
    public pageSize: number,
    public itemCount: number,
    currentPage: number
  ) {
    const reminder = itemCount % pageSize;
    this.totalPages =
      reminder == 0
        ? itemCount / pageSize
        : Math.floor(itemCount / pageSize) + 1;
    currentPage < 1 ? (this.currentPage = 1) : (this.currentPage = currentPage);
  }

  hasPrev(): boolean {
    return this.currentPage > 1;
  }

  hasNext(): boolean {
    return this.currentPage < this.totalPages;
  }

  prev(): PagingStatus | undefined {
    return this.hasPrev()
      ? new PagingStatus(this.pageSize, this.itemCount, this.currentPage - 1)
      : undefined;
  }

  next(): PagingStatus | undefined {
    return this.hasNext()
      ? new PagingStatus(this.pageSize, this.itemCount, this.currentPage + 1)
      : undefined;
  }

  startNumber(): number {
    return this.pageSize * (this.currentPage - 1);
  }

  endNumber(): number {
    const last = this.currentPage * this.pageSize;
    return last > this.itemCount ? this.itemCount : last;
  }

  setPageNumber(pn: number): PagingStatus {
    return new PagingStatus(this.pageSize, this.itemCount, pn);
  }

  setTotalNumber(ic: number): PagingStatus {
    const nextPag = new PagingStatus(this.pageSize, ic, 1);
    if (nextPag.totalPages >= this.currentPage) {
      return nextPag.setPageNumber(this.currentPage);
    }
    return nextPag.setPageNumber(nextPag.totalPages);
  }
}

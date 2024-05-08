import { Injectable } from '@angular/core';
import { ReplaySubject } from 'rxjs';
import {
  CreationTaskDTO,
  ModifierDTO,
  SearchHit,
  SubactDTO,
  TextDTO,
} from '../model';

@Injectable({
  providedIn: 'root',
})
export class FilterService {
  filter$: ReplaySubject<MyFilter> = new ReplaySubject(1);

  filterString: string = '';
  filterFields: string[] = [];

  constructor() {}

  resetFilter(): void {
    this.filterString = '';
    this.emitNext();
  }

  setFilterString(s: string): void {
    this.filterString = s;
    this.emitNext();
  }

  emitNext(): void {
    const filter: MyFilter = new MyFilter(this.filterString);
    this.filter$.next(filter);
  }
}

export class MyFilter {
  static readonly NO_FILTER: MyFilter = new MyFilter('');

  filterString: string;

  constructor(filterString: string) {
    this.filterString = !filterString ? '' : this.normalizeString(filterString);
  }

  private normalizeString(s: string): string {
    return !s
      ? ''
      : s
          .normalize('NFD')
          .replace(/[\u0300-\u036f]/g, '')
          .toLowerCase();
  }

  private stringContains(s: string): boolean {
    return this.normalizeString(s).includes(this.filterString);
  }

  filterCreationTasks(tasks: CreationTaskDTO[]): CreationTaskDTO[] {
    return tasks.filter((task) => this.filterCreationTask(task));
  }
  private filterCreationTask(task: CreationTaskDTO): boolean {
    if (!this.filterString) {
      return true;
    }
    return this.stringContains(task.taskName) || this.stringContains(task.desc);
  }

  filterModifierList(modifierList: ModifierDTO[]) {
    return modifierList.filter((modifier) => this.filterModifier(modifier));
  }
  private filterModifier(modifier: ModifierDTO): boolean {
    if (!this.filterString) {
      return true;
    }
    return (
      this.stringContains(modifier.modifierCode) ||
      this.stringContains(modifier.desc)
    );
  }

  filterSubactList(subactList: SubactDTO[]) {
    return subactList.filter((subact) => this.filterSubact(subact));
  }
  private filterSubact(subact: SubactDTO): boolean {
    if (!this.filterString) {
      return true;
    }
    return (
      this.stringContains(subact.subactName) || this.stringContains(subact.desc)
    );
  }

  filterTextList(textList: TextDTO[]) {
    return textList.filter((text) => this.filterText(text));
  }
  private filterText(text: TextDTO): boolean {
    if (!this.filterString) {
      return true;
    }
    return (
      this.stringContains(text.textdata) ||
      text.languageCode === this.filterString
    );
  }

  filterSearchHit(hits: SearchHit[]) {
    return hits.filter((hit) => this.filterHit(hit));
  }
  private filterHit(hit: SearchHit): boolean {
    if (!this.filterString || !hit?.textMetadata) {
      return true;
    }
    return this.stringContains(hit.textMetadata.textdata);
  }
}

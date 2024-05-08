import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { SortStatus } from 'src/app/service/my-sorting.service';
import { faArrowDown, faArrowUp } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-sort-header',
  templateUrl: './sort-header.component.html',
  styleUrls: ['./sort-header.component.scss'],
})
export class SortHeaderComponent implements OnInit, OnChanges {
  ArrowStatus = ArrowStatus;

  faArrowDown = faArrowDown;
  faArrowUp = faArrowUp;

  @Input()
  columnName: string = '';

  @Input()
  fieldname: string = '';

  @Input()
  sortStatus: SortStatus = new SortStatus(undefined, true);

  arrowStat: ArrowStatus = ArrowStatus.NONE;

  @Output()
  clicked: EventEmitter<string> = new EventEmitter();

  constructor() {}

  ngOnInit(): void {}

  ngOnChanges(changes: SimpleChanges): void {
    if (this.sortStatus.fieldname === this.fieldname) {
      if (this.sortStatus.asc) {
        this.arrowStat = ArrowStatus.UP;
      } else {
        this.arrowStat = ArrowStatus.DOWN;
      }
    } else {
      this.arrowStat = ArrowStatus.NONE;
    }
  }

  emitField(): void {
    this.clicked.emit(this.fieldname);
  }
}

enum ArrowStatus {
  NONE,
  UP,
  DOWN,
}

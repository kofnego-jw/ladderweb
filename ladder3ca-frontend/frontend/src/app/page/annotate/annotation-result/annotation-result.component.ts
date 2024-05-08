import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { AnnotatingResultDTO, AppRole } from 'src/app/model';
import { AppService } from 'src/app/service/app.service';
import { EditTextStateService } from 'src/app/service/edit-text-state.service';
import { MyModalService } from 'src/app/service/my-modal.service';

@Component({
  selector: 'app-annotation-result',
  templateUrl: './annotation-result.component.html',
  styleUrls: ['./annotation-result.component.scss'],
})
export class AnnotationResultComponent implements OnInit, OnDestroy {
  destroyed$: Subject<boolean> = new Subject<boolean>();
  @Input() result: AnnotatingResultDTO | undefined = undefined;

  canUser: boolean = false;

  constructor(
    private editText: EditTextStateService,
    private app: AppService
  ) {}

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }
  ngOnInit(): void {
    this.app.user$.pipe(takeUntil(this.destroyed$)).subscribe((user) => {
      if (user && user?.roles.indexOf(AppRole.USER) > -1) {
        this.canUser = true;
      } else {
        this.canUser = false;
      }
    });
  }

  addAsNewText(): void {
    if (this.result?.result?.originalText) {
      this.editText.addToDatabase(this.result.result.originalText);
    }
  }
}

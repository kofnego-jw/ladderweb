import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { Subject, takeUntil, timer } from 'rxjs';
import {
  Language,
  ModelMessage,
  ModelType,
  ModifierDTO,
  SimpleMessageDTO,
  SubactDTO,
} from 'src/app/model';
import { AppService } from 'src/app/service/app.service';
import { MyModalService } from 'src/app/service/my-modal.service';

@Component({
  selector: 'app-pos-model',
  templateUrl: './pos-model.component.html',
  styleUrls: ['./pos-model.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PosModelComponent implements OnInit, OnDestroy {
  destroyed$: Subject<boolean> = new Subject();
  languages = [Language.de, Language.it];

  modifierList: ModifierDTO[] = [];
  subactList: SubactDTO[] = [];

  modelMessages: ModelMessage[] = [];

  isTraining: boolean = false;

  constructor(
    private app: AppService,
    private modal: MyModalService,
    private changeDetector: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.app.mod_listAll().subscribe((list) => {
      this.modifierList = list;
      this.changeDetector.markForCheck();
    });
    this.app.sub_listAll().subscribe((list) => {
      this.subactList = list;
      this.changeDetector.markForCheck();
    });
    timer(0, 1000 * 60 * 2)
      .pipe(takeUntil(this.destroyed$))
      .subscribe(() => {
        this.app.pos_trainingMessages().subscribe((list) => {
          console.log('list', list);
          this.modelMessages = list ? list.modelMessageList : [];
          this.changeDetector.markForCheck();
        });
        this.app.pos_trainIsRunning().subscribe((isRunning) => {
          this.isTraining = isRunning;
          this.changeDetector.markForCheck();
        });
      });
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  retrainAll(): void {
    this.app.pos_trainAll().subscribe((msg) => {
      this.handleMessage(msg, 'Retrain All');
      this.app.pos_trainIsRunning().subscribe((isRunning) => {
        this.isTraining = isRunning;
        this.changeDetector.markForCheck();
      });
    });
  }

  handleMessage(msg: SimpleMessageDTO | undefined, task: string) {
    if (!msg) {
      this.modal.openInfoModal('Error', 'Exception during ' + task);
    } else if (msg.errorMessages.length == 0) {
      this.modal.openInfoModal('Message', task + ' results in: ' + msg.message);
    } else {
      let errorMsg = task + ' results in: ' + msg.message + '<br/><br/>';
      for (let error of msg.errorMessages) {
        errorMsg = errorMsg + error + '<br/>';
      }
      this.modal.openInfoModal('Error', errorMsg);
    }
  }

  getModifierModelMessage(mod: ModifierDTO, language: string): string[] {
    const msg = this.modelMessages.find(
      (x) =>
        x.type === ModelType.MODIFIER &&
        x.modelName === mod.modifierCode &&
        x.language === language
    );
    console.log('msg', msg);
    return msg ? msg.messages : [];
  }

  getSubactModelMessage(sub: SubactDTO, language: string): string[] {
    const msg = this.modelMessages.find(
      (x) =>
        x.type === ModelType.SUBACT &&
        x.modelName === sub.subactName &&
        x.language === language
    );
    return msg ? msg.messages : [];
  }
}

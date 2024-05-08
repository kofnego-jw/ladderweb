import {
  AfterViewInit,
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { TabsetComponent } from 'ngx-bootstrap/tabs';
import { Subject, takeUntil } from 'rxjs';
import {
  AnnotatingResultDTO,
  ModifierAnnotationDTO,
  ModifierDTO,
  SubactDTO,
  Language,
  SubactDisplay,
} from 'src/app/model';
import * as bootstrap from 'bootstrap';
import { AppService } from 'src/app/service/app.service';

@Component({
  selector: 'app-annotate',
  templateUrl: './annotate.component.html',
  styleUrls: ['./annotate.component.scss'],
})
export class AnnotateComponent implements OnInit, OnDestroy {
  destroyed$: Subject<boolean> = new Subject<boolean>();

  annotateForm: FormGroup;
  availableModifiers: ModifierDTO[] = [];
  availableSubacts: SubactDTO[] = [];
  availableLanguages: string[] = [Language.de, Language.it];
  selectedModifiers: ModifierDTO[] = [];
  selectedSubacts: SubactDTO[] = [];

  displayedSubacts: SubactDisplay[] = [];

  result: AnnotatingResultDTO | undefined = undefined;

  @ViewChild('annotateTabs', { static: false }) annotateTabs:
    | TabsetComponent
    | undefined;

  constructor(private fb: FormBuilder, private app: AppService) {
    this.annotateForm = this.fb.group({
      text: [''],
      useMax: [false],
      language: [],
    });
  }

  setActiveTab(tabName: string): void {}

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  ngOnInit(): void {
    this.app.modifiers$
      .pipe(takeUntil(this.destroyed$))
      .subscribe((modifiers) => {
        this.availableModifiers = modifiers;
        for (let modifier of modifiers) {
          this.selectedModifiers.push(modifier);
        }
      });
    this.app.subacts$.pipe(takeUntil(this.destroyed$)).subscribe((subacts) => {
      this.availableSubacts = subacts;
      for (let subact of subacts) {
        this.selectedSubacts.push(subact);
      }
      this.createDisplayedSubacts();
    });
  }

  createDisplayedSubacts(): void {
    this.displayedSubacts = [];
    const topLevelSubacts = this.availableSubacts.filter(
      (sa) => !sa.parentSubactId
    );
    for (let subact of topLevelSubacts) {
      const subs = this.availableSubacts.filter(
        (sa) => sa.parentSubactId === subact.id
      );
      this.displayedSubacts.push(new SubactDisplay(subact, subs));
    }
  }

  modifierIsSelected(modifier: ModifierDTO): boolean {
    return this.selectedModifiers.includes(modifier);
  }

  toggleModifier(modifier: ModifierDTO): void {
    if (this.modifierIsSelected(modifier)) {
      this.selectedModifiers = this.selectedModifiers.filter(
        (m) => m !== modifier
      );
    } else {
      this.selectedModifiers.push(modifier);
    }
  }

  subactIsSelected(subact: SubactDTO): boolean {
    return this.selectedSubacts.includes(subact);
  }

  toggleSubact(subact: SubactDTO): void {
    if (this.subactIsSelected(subact)) {
      this.selectedSubacts = this.selectedSubacts.filter((m) => m !== subact);
      const selectedSubs = this.selectedSubacts.filter(
        (m) => m.parentSubactId === subact.id
      );
      selectedSubs.forEach((s) => {
        this.selectedSubacts = this.selectedSubacts.filter((m) => m !== s);
      });
    } else {
      this.selectedSubacts.push(subact);
      const subs = this.availableSubacts.filter(
        (sa) => sa.parentSubactId === subact.id
      );
      subs.forEach((s) => {
        if (!this.selectedSubacts.includes(s)) {
          this.selectedSubacts.push(s);
        }
      });
    }
  }

  selectAllModifiers(): void {
    this.availableModifiers.forEach((m) => {
      if (!this.selectedModifiers.includes(m)) {
        this.toggleModifier(m);
      }
    });
  }

  selectAllSubacts(): void {
    this.availableSubacts.forEach((m) => {
      if (!this.selectedSubacts.includes(m)) {
        this.toggleSubact(m);
      }
    });
  }

  setResult(result: AnnotatingResultDTO | undefined): void {
    this.result = result;
    if (this.result) {
      this.selectTab(1);
    }
  }

  doAnnotate(): void {
    const text = this.annotateForm.get('text')?.value;
    const useMax = this.annotateForm.get('useMax')?.value;
    const langCode = this.annotateForm.get('language')?.value;
    this.app
      .annotate(
        text,
        useMax,
        langCode,
        this.selectedModifiers,
        this.selectedSubacts
      )
      .subscribe((result) => this.setResult(result));
  }

  selectTab(tabId: number) {
    if (this.annotateTabs?.tabs[tabId]) {
      this.annotateTabs.tabs[tabId].active = true;
    }
  }
}

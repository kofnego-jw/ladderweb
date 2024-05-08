import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { faPencil } from '@fortawesome/free-solid-svg-icons';
import { Subject, takeUntil } from 'rxjs';
import {
  ModifierAnnotationDTO,
  ModifierDTO,
  SubactAnnotationDTO,
  SubactDTO,
  TextWithMetadataDTO,
  TokenInfo,
} from 'src/app/model';
import { AppService } from 'src/app/service/app.service';
import { EditTextStateService } from 'src/app/service/edit-text-state.service';
import { MyModalService } from 'src/app/service/my-modal.service';

@Component({
  selector: 'app-annotation',
  templateUrl: './annotation.component.html',
  styleUrls: ['./annotation.component.scss'],
})
export class AnnotationComponent implements OnInit, OnDestroy {
  destroyed$: Subject<boolean> = new Subject();

  faPencil = faPencil;

  text: TextWithMetadataDTO | undefined;

  modifierList: ModifierDTO[] = [];
  subactList: SubactDTO[] = [];

  modifierAnnotations: ModifierAnnotationDTO[] = [];
  subactAnnotations: SubactAnnotationDTO[] = [];

  changed: boolean = false;

  constructor(
    private editText: EditTextStateService,
    private router: Router,
    private app: AppService,
    private modal: MyModalService
  ) {}

  ngOnDestroy(): void {
    this.destroyed$.next(true);
  }

  ngOnInit(): void {
    this.app.mod_listAll().subscribe((list) => (this.modifierList = list));
    this.app.sub_listAll().subscribe((list) => (this.subactList = list));
    this.editText.currentText$
      .pipe(takeUntil(this.destroyed$))
      .subscribe((dto) => {
        this.text = dto;
        this.loadAnnotations();
      });
  }

  setModifierAnnotations(list: ModifierAnnotationDTO[]) {
    this.modifierAnnotations = [...list];
  }

  setSubactAnnotations(list: SubactAnnotationDTO[]) {
    this.subactAnnotations = [...list];
  }

  loadAnnotations(): void {
    if (this.text?.id) {
      this.app
        .ann_listModifiersByText(this.text.id)
        .subscribe((list) => this.setModifierAnnotations(list));
      this.app
        .ann_listSubactsByText(this.text.id)
        .subscribe((list) => this.setSubactAnnotations(list));
    }
  }

  addTokenInfos(tokens: TokenInfo[]): void {
    if (this.text && tokens.length) {
      for (let i = 0; i < tokens.length; i++) {
        const tokenInfo = tokens[i];
        tokenInfo.modifierIds.forEach((modId) => {
          if (this.text?.id) {
            const toAdd = new ModifierAnnotationDTO(
              0,
              this.text.id,
              modId,
              i,
              i
            );
            if (
              !this.modifierAnnotations.find(
                (x) =>
                  x.modifierId === toAdd.modifierId &&
                  x.startTn === toAdd.startTn &&
                  x.endTn === toAdd.endTn
              )
            ) {
              this.modifierAnnotations.push(toAdd);
            }
          }
        });
        tokenInfo.subactIds.forEach((modId) => {
          if (this.text?.id) {
            const toAdd = new SubactAnnotationDTO(0, this.text.id, modId, i, i);
            if (
              !this.subactAnnotations.find(
                (x) =>
                  x.subactId === toAdd.subactId &&
                  x.startTn === toAdd.startTn &&
                  x.endTn === toAdd.endTn
              )
            ) {
              this.subactAnnotations.push(toAdd);
            }
          }
        });
      }
    }
  }

  applyModels(useMax: boolean): void {
    if (this.text?.id) {
      this.app
        .annotate(
          this.text.textdata,
          useMax,
          this.text.languageCode,
          this.modifierList,
          this.subactList
        )
        .subscribe((result) => {
          if (result?.result) {
            this.addTokenInfos(result.result.tokenInfos);
          }
          this.changed = true;
        });
    }
  }

  modifierAnnotationAt(index: number): ModifierDTO[] {
    const list = this.modifierAnnotations.filter(
      (ann) => ann.startTn <= index && index <= ann.endTn
    );
    return list
      .map((ann) => this.modifierList.find((mod) => mod.id === ann.modifierId))
      .filter((mod): mod is ModifierDTO => !!mod);
  }

  editModifierAnnotationAt(token: string, index: number): void {
    this.changed = true;
    const selectedModifiers = this.modifierAnnotationAt(index);
    this.modifierAnnotations = this.modifierAnnotations.filter(
      (x) => x.startTn !== index && x.endTn !== index
    );
    this.modal
      .selectModifiers(token, this.modifierList, selectedModifiers)
      .subscribe((list) => {
        if (list && list.length && this.text?.id) {
          const textId = this.text.id;
          list.forEach((mod) => {
            if (mod.id) {
              this.modifierAnnotations.push(
                new ModifierAnnotationDTO(0, textId, mod.id, index, index)
              );
            }
          });
        }
      });
  }

  subactAnnotationAt(index: number): SubactDTO[] {
    const list = this.subactAnnotations.filter(
      (ann) => ann.startTn <= index && index <= ann.endTn
    );
    return list
      .map((ann) => this.subactList.find((sub) => sub.id === ann.subactId))
      .filter((sub): sub is SubactDTO => !!sub);
  }

  editSubactAnnotationAt(token: string, index: number): void {
    this.changed = true;
    const selectedSubacts = this.subactAnnotationAt(index);
    this.subactAnnotations = this.subactAnnotations.filter(
      (x) => x.startTn !== index && x.endTn !== index
    );
    this.modal
      .selectSubacts(token, this.subactList, selectedSubacts)
      .subscribe((list) => {
        if (list && list.length && this.text?.id) {
          const textId = this.text.id;
          list.forEach((sub) => {
            if (sub.id) {
              this.subactAnnotations.push(
                new SubactAnnotationDTO(0, textId, sub.id, index, index)
              );
            }
          });
        }
      });
  }

  save(): void {
    if (this.text?.id) {
      const textId = this.text.id;
      this.app
        .ann_saveModifierAnnotations(textId, this.modifierAnnotations)
        .subscribe((dto) => {
          this.app
            .ann_saveSubactAnnotations(textId, this.subactAnnotations)
            .subscribe((dto) => {
              this.loadAnnotations();
              this.changed = false;
            });
        });
    }
  }
}

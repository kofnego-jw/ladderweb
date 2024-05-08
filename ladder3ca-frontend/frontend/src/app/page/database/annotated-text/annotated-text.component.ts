import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { TabDirective, TabsetComponent } from 'ngx-bootstrap/tabs';
import { Subject, takeUntil } from 'rxjs';
import { Location } from '@angular/common';

@Component({
  selector: 'app-annotated-text',
  templateUrl: './annotated-text.component.html',
  styleUrls: ['./annotated-text.component.scss'],
})
export class AnnotatedTextComponent implements OnInit, OnDestroy {
  destroyed$: Subject<boolean> = new Subject();

  @ViewChild('textTabset', { static: false }) textTabset:
    | TabsetComponent
    | undefined;

  constructor(private router: Router, private location: Location) {}

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  ngOnInit(): void {
    this.router.events.pipe(takeUntil(this.destroyed$)).subscribe((ev) => {
      if (ev instanceof NavigationEnd) {
        const ne = ev as NavigationEnd;
        if (ne.url.includes('annotation') && this.textTabset?.tabs) {
          this.textTabset.tabs[2].active = true;
        }
      }
    });
  }

  onSelect(ev: TabDirective): void {
    this.router.navigate(['/database/text']);
  }
}

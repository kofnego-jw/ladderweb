import {
  AfterViewInit,
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import * as bootstrap from 'bootstrap';
import { Subject, takeUntil } from 'rxjs';
import { OffCanvasAction } from 'src/app/model';
import { AppService } from 'src/app/service/app.service';

@Component({
  selector: 'app-off-canvas',
  templateUrl: './off-canvas.component.html',
  styleUrls: ['./off-canvas.component.scss'],
})
export class OffCanvasComponent implements OnInit, OnDestroy, AfterViewInit {
  destroyed$: Subject<boolean> = new Subject();

  @ViewChild('myOffCanvas')
  myOffCanvas: ElementRef | undefined;
  myOffCanvasBS: bootstrap.Offcanvas | undefined;

  constructor(private router: Router) {}

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  ngOnInit(): void {
    this.router.events.pipe(takeUntil(this.destroyed$)).subscribe((event) => {
      if (event instanceof NavigationEnd) {
        if (this.myOffCanvasBS) {
          this.myOffCanvasBS.hide();
        }
      }
    });
  }

  ngAfterViewInit(): void {
    if (this.myOffCanvas) {
      this.myOffCanvasBS = new bootstrap.Offcanvas(
        this.myOffCanvas.nativeElement
      );
    }
  }

  toggleMyOffCanvas(): void {
    if (!this.myOffCanvasBS) {
      return;
    }
    this.myOffCanvasBS.toggle();
  }

  offCanvasAction(action: OffCanvasAction): void {
    if (!this.myOffCanvasBS) {
      return;
    }
    if (action === OffCanvasAction.OPEN) {
      this.myOffCanvasBS.show();
    } else if (action === OffCanvasAction.CLOSE) {
      this.myOffCanvasBS.hide();
    } else {
      this.myOffCanvasBS.toggle();
    }
  }
}

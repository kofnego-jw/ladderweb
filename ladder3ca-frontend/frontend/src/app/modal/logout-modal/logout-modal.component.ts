import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { AppService } from 'src/app/service/app.service';

@Component({
  selector: 'app-logout-modal',
  templateUrl: './logout-modal.component.html',
  styleUrls: ['./logout-modal.component.scss'],
})
export class LogoutModalComponent implements OnInit {
  constructor(
    private modalRef: BsModalRef,
    private app: AppService,
    private router: Router
  ) {}

  ngOnInit(): void {}

  close(): void {
    this.modalRef.hide();
  }

  logout(): void {
    this.app.logout();
    this.router.navigate(['/']);
    this.close();
  }
}

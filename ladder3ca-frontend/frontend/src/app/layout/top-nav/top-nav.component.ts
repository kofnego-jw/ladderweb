import { Component, OnDestroy, OnInit } from '@angular/core';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subject, takeUntil } from 'rxjs';
import { ChangePasswordModalComponent } from 'src/app/modal/change-password-modal/change-password-modal.component';
import { LoginModalComponent } from 'src/app/modal/login-modal/login-modal.component';
import { LogoutModalComponent } from 'src/app/modal/logout-modal/logout-modal.component';
import { AppRole, AppUserDTO } from 'src/app/model';
import { AppService } from 'src/app/service/app.service';

@Component({
  selector: 'app-top-nav',
  templateUrl: './top-nav.component.html',
  styleUrls: ['./top-nav.component.scss'],
})
export class TopNavComponent implements OnInit, OnDestroy {
  destroyed$: Subject<boolean> = new Subject();

  canUser: boolean = false;
  canAdmin: boolean = false;

  username: string = '';

  navbarCollapsed = true;

  constructor(private app: AppService, private modalService: BsModalService) {}

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  ngOnInit(): void {
    this.app.user$
      .pipe(takeUntil(this.destroyed$))
      .subscribe((user) => this.setAppUser(user));
  }

  toggleNavbar(): void {
    this.navbarCollapsed = !this.navbarCollapsed;
  }

  setAppUser(user: AppUserDTO | undefined): void {
    if (user) {
      this.canUser = user.roles.find((x) => x === AppRole.USER) !== undefined;
      this.canAdmin = user.roles.find((x) => x === AppRole.ADMIN) !== undefined;
      this.username = user.emailAddress;
    } else {
      this.canUser = false;
      this.canAdmin = false;
      this.username = '';
    }
  }

  showLoginForm(): void {
    this.modalService.show(LoginModalComponent);
  }

  showLogoutForm(): void {
    this.modalService.show(LogoutModalComponent);
  }

  showChangePasswordForm(): void {
    this.modalService.show(ChangePasswordModalComponent, {
      initialState: { user: this.app.user },
    });
  }
}

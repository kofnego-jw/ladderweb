import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { AppUserDTO, SimpleMessageDTO } from 'src/app/model';
import { AppService } from 'src/app/service/app.service';

@Component({
  selector: 'app-change-password-modal',
  templateUrl: './change-password-modal.component.html',
  styleUrls: ['./change-password-modal.component.scss'],
})
export class ChangePasswordModalComponent implements OnInit {
  @Input()
  user: AppUserDTO | undefined;

  changePasswordForm: FormGroup;

  constructor(
    fb: FormBuilder,
    private modalRef: BsModalRef,
    private app: AppService
  ) {
    this.changePasswordForm = fb.group({
      userId: [],
      oldPassword: [],
      newPassword: [],
      newPasswordAgain: [],
    });
  }

  ngOnInit(): void {
    if (!this.user) {
      this.close();
    }
    this.changePasswordForm.get('userId')?.setValue(this.user?.id);
  }

  close(): void {
    this.modalRef.hide();
  }

  changePassword(): void {
    if (this.changePasswordForm.touched && this.changePasswordForm.valid) {
      const changeOwn = this.user?.id === this.app.user?.id;
      const id = this.changePasswordForm.get('userId')?.value;
      const oldPassword = this.changePasswordForm.get('oldPassword')?.value;
      const newPassword = this.changePasswordForm.get('newPassword')?.value;
      const newPasswordAgain =
        this.changePasswordForm.get('newPasswordAgain')?.value;
      if (newPassword !== newPasswordAgain) {
        this.app.showMessage(
          new SimpleMessageDTO(500, 'New passwords do not match.', [])
        );
        return;
      }
      if (changeOwn) {
        this.app.user_changeOwnPassword(oldPassword, newPassword);
        this.close();
      } else {
        this.app
          .adm_changePassword(id, newPassword)
          .subscribe((msg) => this.close());
      }
    }
    this.close();
  }
}

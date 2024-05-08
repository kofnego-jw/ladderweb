import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { AppRole, AppUserDTO } from 'src/app/model';
import { AppService } from 'src/app/service/app.service';

@Component({
  selector: 'app-update-user-modal',
  templateUrl: './update-user-modal.component.html',
  styleUrls: ['./update-user-modal.component.scss'],
})
export class UpdateUserModalComponent implements OnInit {
  @Input()
  user: AppUserDTO | undefined;

  updateUserForm: FormGroup;

  roles: AppRole[] = [AppRole.USER, AppRole.ADMIN];

  selectedRoles: AppRole[] = [];

  constructor(
    fb: FormBuilder,
    private modalRef: BsModalRef,
    private app: AppService
  ) {
    this.updateUserForm = fb.group({
      emailAddress: [],
      password: [],
    });
  }

  ngOnInit(): void {
    if (!this.user) {
      this.close();
      return;
    }
    this.updateUserForm.get('emailAddress')?.setValue(this.user?.emailAddress);
    this.selectedRoles = [...this.user.roles];
  }

  toggleSelectedRole(role: AppRole): void {
    const index = this.selectedRoles.indexOf(role);
    if (index === -1) {
      this.selectedRoles.push(role);
    } else {
      this.selectedRoles.splice(index, 1);
    }
    this.selectedRoles = [...this.selectedRoles];
  }

  isSelectedRole(role: AppRole): boolean {
    return this.selectedRoles.indexOf(role) !== -1;
  }

  close(): void {
    this.modalRef.hide();
  }

  updateUser(): void {
    if (this.updateUserForm.valid && this.user) {
      const email = this.updateUserForm.get('emailAddress')?.value;
      this.app
        .adm_updateUser(this.user?.id, email, this.selectedRoles)
        .subscribe(() => this.close());
    }
  }
}

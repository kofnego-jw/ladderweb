import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { AppRole } from 'src/app/model';
import { AppService } from 'src/app/service/app.service';

@Component({
  selector: 'app-add-user-modal',
  templateUrl: './add-user-modal.component.html',
  styleUrls: ['./add-user-modal.component.scss'],
})
export class AddUserModalComponent implements OnInit {
  addUserForm: FormGroup;

  roles: AppRole[] = [AppRole.USER, AppRole.ADMIN];

  selectedRoles: AppRole[] = [];

  constructor(
    fb: FormBuilder,
    private modalRef: BsModalRef,
    private app: AppService
  ) {
    this.addUserForm = fb.group({
      emailAddress: [],
      password: [],
    });
  }

  ngOnInit(): void {}

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

  addUser(): void {
    if (this.addUserForm.touched && this.addUserForm.valid) {
      const email = this.addUserForm.get('emailAddress')?.value;
      const password = this.addUserForm.get('password')?.value;
      this.app
        .adm_createUser(email, password, this.selectedRoles)
        .subscribe(() => this.close());
    }
    this.close();
  }
}

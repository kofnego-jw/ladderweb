import { Component, OnInit } from '@angular/core';
import { BsModalService } from 'ngx-bootstrap/modal';
import { AddUserModalComponent } from 'src/app/modal/add-user-modal/add-user-modal.component';
import { ChangePasswordModalComponent } from 'src/app/modal/change-password-modal/change-password-modal.component';
import { UpdateUserModalComponent } from 'src/app/modal/update-user-modal/update-user-modal.component';
import { AppUserDTO } from 'src/app/model';
import { AppService } from 'src/app/service/app.service';

@Component({
  selector: 'app-app-user-list',
  templateUrl: './app-user-list.component.html',
  styleUrls: ['./app-user-list.component.scss'],
})
export class AppUserListComponent implements OnInit {
  users: AppUserDTO[] = [];

  constructor(private app: AppService, private modal: BsModalService) {}

  ngOnInit(): void {
    this.reloadUserList();
  }

  reloadUserList(): void {
    this.app.adm_listAllUsers().subscribe((users) => this.setUsers(users));
  }

  setUsers(users: AppUserDTO[]): void {
    this.users = users;
  }

  changePassword(user: AppUserDTO): void {
    this.modal.show(ChangePasswordModalComponent, {
      initialState: { user },
    });
  }

  updateUser(user: AppUserDTO): void {
    const updateUser = this.modal.show(UpdateUserModalComponent, {
      initialState: { user: user },
    });
    updateUser.onHide?.subscribe(() => this.reloadUserList());
  }

  addUser(): void {
    const addUser = this.modal.show(AddUserModalComponent);
    addUser.onHide?.subscribe(() => this.reloadUserList());
  }
}

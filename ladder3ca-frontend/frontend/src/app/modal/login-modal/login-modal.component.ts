import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { AppService } from 'src/app/service/app.service';

@Component({
  selector: 'app-login-modal',
  templateUrl: './login-modal.component.html',
  styleUrls: ['./login-modal.component.scss'],
})
export class LoginModalComponent implements OnInit {
  loginForm: FormGroup;

  constructor(
    fb: FormBuilder,
    private modalRef: BsModalRef,
    private app: AppService
  ) {
    this.loginForm = fb.group({
      username: [],
      password: [],
    });
  }

  ngOnInit(): void {
    if (this.app.user) {
      this.loginForm.disable();
    }
  }

  close(): void {
    this.modalRef.hide();
  }

  login(): void {
    if (this.loginForm.touched && this.loginForm.valid) {
      const username = this.loginForm.get('username')?.value;
      const password = this.loginForm.get('password')?.value;
      this.app.login(username, password);
    }
    this.close();
  }
}

import { Component, OnInit } from '@angular/core';
import { faBars } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-side-bar',
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.scss'],
})
export class SideBarComponent implements OnInit {
  faBars = faBars;
  menuVisible: boolean = false;

  constructor() {}

  ngOnInit(): void {}

  toggleMenuVisible(): void {
    this.menuVisible = !this.menuVisible;
  }
}

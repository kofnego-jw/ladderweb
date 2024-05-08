import { AfterViewInit, Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements AfterViewInit {
  ngAfterViewInit(): void {
    const navbarCollapse = document.querySelector('#navbarSideCollapse');
    const offcanvasToggle = document.querySelector('.offcanvas-collapse');
    if (navbarCollapse && offcanvasToggle) {
      navbarCollapse.addEventListener('click', () => {
        offcanvasToggle.classList.toggle('open');
      });
    }
  }
}


(() => {
  'use strict'

  document.querySelector('#navbarSideCollapse').addEventListener('click', () => {
    document.querySelector('.offcanvas-collapse').classList.toggle('open')
  })
})()


/*
(function () {
  'use strict'

  document.querySelector('#navbarSideCollapse').addEventListener('click', function () {
    document.querySelector('.offcanvas-collapse').classList.toggle('open')
  })
})()
*/

/*
var toggle = document.getElementById("toggleSidebar")
var offcanvas_el = document.getElementById("offcanvasExample")
var offcanvas = new bootstrap.Offcanvas(offcanvas_el, {backdrop: false})

toggle.addEventListener("change", function(){
    toggle.checked ? offcanvas.show() : offcanvas.hide() 
})

// handle case when sidebar is closed internally using `X`
offcanvas_el.addEventListener('hide.bs.offcanvas', function () {
    toggle.checked = false
})
*/
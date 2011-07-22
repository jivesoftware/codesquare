/*
  $(document).ready(function() {
  alert("hi");
  // moved the function below from here to init() in main.js for local testing
  //	makeBadgeTable();
  $(function() {
  
  $("form").submit(function() {

  $form = $(this);
  $.fancybox({
  'href': $form.attr("action") + "?" + $form.serialize(),
  'transitionIn' : 'elastic',
  'transitionOut' : 'elastic',
  'autoScale' : false,
  'width': '40%',
  'height': '100%',
  'type': 'iframe',
  
  });

  return false;

  });

  });

  });
*/
/*
//implementation for lightbox_me
$('.badgeLink').click(function(e) {
$('.popup').lightbox_me({
centered: true, 
});
e.preventDefault();
});  
*/

$('#browse a').click(function(e) {
    changePage('subpages/browse.html');
    makeBadgeTable(glObj.email,glObj.bossEmail);
    tab = $('#browse');
    tab.addClass('current-cat');
    tab.siblings().removeClass('current-cat');
});

$('#share a').click(function(e) {
    changePage('subpages/share.html');
    tab = $('#share');
    tab.addClass('current-cat');
    tab.siblings().removeClass('current-cat');
});

$('#brag a').click(function(e) {
    changePage('subpages/brag.html');
    tab = $('#brag');
    tab.addClass('current-cat');
    tab.siblings().removeClass('current-cat');
});
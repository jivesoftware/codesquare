$('#browse a').click(function(e) {
    changePage('subpages/browse.html');
    makeBadgeTable(GLOBJ.email);
    document.getElementById('lsidebar-wrapper').innerHTML="";
    tab = $('#browse');
    tab.addClass('current-cat');
    tab.siblings().removeClass('current-cat');
});

$('#compare a').click(function(e) {
    changePage('subpages/compare.html');
    document.getElementById('lsidebar-wrapper').innerHTML="";
    getFollowersToCompare(); // enableAutoComplete() in here
    tab = $('#compare');
    tab.addClass('current-cat');
    tab.siblings().removeClass('current-cat');
});


$('#brag a').click(function(e) {
    changePage('subpages/brag.html');
    document.getElementById('lsidebar-wrapper').innerHTML="";
    tab = $('#brag');
    tab.addClass('current-cat');
    tab.siblings().removeClass('current-cat');
    bragBasics(); // enableAutoComplete() in here
});
// processor that makes the main badge list from the given json, and optionally a friendName 

function makeBadgeTable(jsonObj, friendName) {
    var totalBadges = 0;
    var newBadges = 0;
    var tableHTML = "";
    var badgeCountHTML = "";
    var stateClass = "shadow";
    var key, value, imgURL, thumbnailUrl, popupURl;

    for (key = 1; key <= Object.size(jsonObj); key++) {
	value = jsonObj[key];
	if (key % 5 === 1) {
	    tableHTML += "<tr>";
	}
	imgURL = fullURL(value.IconURL);
	console.log(imgURL);
	thumbnailUrl = fullURL(value.thumbnail);
	popupURL = fullURL("badgePopup.html");
	tableHTML += "<td>" +
	    "<form class='badges' action='" + popupURL + "' method='GET'>" +
	    "<input type='hidden' name='imgURL' value='" + imgURL + "'/>" +
	    "<input type='hidden' name='name' value='" + value.Name + "'/>" +
	    "<input type='hidden' name='desc' value='" + value.Description + "'/>";
	if (value.IconURL !== "images/unobtained.png") {
	    totalBadges = totalBadges + 1;
	}
	if (value.New == "true") {
	    stateClass = "highlight";
	    newBadges = newBadges + 1;
	}
	tableHTML+= "<input class='" + stateClass + "' type='image' src='" + thumbnailUrl + "' value='Popup!' alt='Show badge info'/></form></td>";
	if (key % 5 === 0) {
	    tableHTML += "</tr>";
	}
    }

    document.getElementById('badgeTable').innerHTML = tableHTML;
    // this snippet of code is used for loading friend's name in compare tab
    if (friendName) {
	console.log('here is the friend name');
	console.log(friendName);
	// assume that arguments[1] contains the name of the friend
	badgeCountHTML += friendName +" has "+totalBadges+" badges.";
    } else {
	if (newBadges > 0) {
	    badgeCountHTML += "You earned "+newBadges+" new badges! ";
	} 
	badgeCountHTML += "You currently have "+totalBadges+" badges.";
    }
    document.getElementById('numberOfBadges').innerHTML = badgeCountHTML;
    gadgets.window.adjustHeight();
}

// loads the browse badges tab
function loadBrowseBadges() {
    var paramObj = {
	    email : GLOBAL.email
    };
    getBadgeList(paramObj, makeBadgeTable);
}

function loadCanvas(user, boss) {
    // setting up some global vars, this should be the only place to set these evil variables
    //GLOBAL.name = user.name;
    GLOBAL.email = user.email;
    //GLOBAL.bossEmail = boss.email;
    //GLOBAL.id = user.id;
    /*console.log(GLOBAL.name);
      console.log(GLOBAL.email);
      console.log(GLOBAL.bossEmail);
      console.log(GLOBAL.id);
    */
    
    loadBrowseBadges();
    
    // JQuery event binding for badge popups
    $("form.badges")
	.live('submit', function(e) {
	    $form = $(this);
	    $.fancybox({
		'href': $form.attr("action") + "?" + $form.serialize(),
		'transitionIn' : 'elastic',
		'transitionOut' : 'elastic',
		'autoScale' : false,
		'width': '40%',
		'height': '60%',
		'type': 'iframe',
	    });
	    //$.fancybox.resize();
	    e.preventDefault();
	    return false;
	});
}


$('#browse a').click(function(e) {
    changePage('subpages/browse.html');
    
    document.getElementById('lsidebar-wrapper').innerHTML="";
    tab = $('#browse');
    tab.addClass('current-cat');
    tab.siblings().removeClass('current-cat');

    loadBrowseBadges();
});

$('#compare a').click(function(e) {
    changePage('subpages/compare.html');
    
    document.getElementById('lsidebar-wrapper').innerHTML="";
    tab = $('#compare');
    tab.addClass('current-cat');
    tab.siblings().removeClass('current-cat');

    getFollowersToCompare(); // enableAutoComplete() in here
});


$('#brag a').click(function(e) {
    changePage('subpages/brag.html');
    
    document.getElementById('lsidebar-wrapper').innerHTML="";
    tab = $('#brag');
    tab.addClass('current-cat');
    tab.siblings().removeClass('current-cat');

    bragBasics(); // enableAutoComplete() in here
});
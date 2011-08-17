function enablePretty() {
    // We only want these styles applied when javascript is enabled
    $('div.navigation').css({'width' : '250px', 'float' : 'left'});
    $('div.content').css('display', 'block');

    // Initially set opacity on thumbs and add
    // additional styling for hover effect on thumbs
    var onMouseOutOpacity = 0.67;
    $('#thumbs ul.thumbs li').opacityrollover({
	mouseOutOpacity:   onMouseOutOpacity,
	mouseOverOpacity:  1.0,
	fadeSpeed:         'fast',
	exemptionSelector: '.selected'
    });
    
    // Initialize Advanced Galleriffic Gallery
    var gallery = $('#thumbs').galleriffic({
	delay:                     2500,
	numThumbs:                 15,
	preloadAhead:              10,
	enableTopPager:            true,
	enableBottomPager:         true,
	maxPagesToShow:            7,
	imageContainerSel:         '#slideshow',
	controlsContainerSel:      '#controls',
	captionContainerSel:       '#caption',
	loadingContainerSel:       '#loading',
	renderSSControls:          true,
	renderNavControls:         true,
	playLinkText:              'Play Slideshow',
	pauseLinkText:             'Stop Slideshow',
	prevLinkText:              '&lsaquo; Previous',
	nextLinkText:              'Next &rsaquo;',
	nextPageLinkText:          'Next &rsaquo;',
	prevPageLinkText:          '&lsaquo; Prev',
	enableHistory:             true,
	autoStart:                 false,
	syncTransitions:           true,
	defaultTransitionDuration: 900,
	onSlideChange:             function(prevIndex, nextIndex) {
	    // 'this' refers to the gallery, which is an extension of $('#thumbs')
	    this.find('ul.thumbs').children()
		.eq(prevIndex).fadeTo('fast', onMouseOutOpacity).end()
		.eq(nextIndex).fadeTo('fast', 1.0);
	},
	onPageTransitionOut:       function(callback) {
	    this.fadeTo('fast', 0.0, callback);
	},
	onPageTransitionIn:        function() {
	    this.fadeTo('fast', 1.0);
	}
    });

    /**** Functions to support integration of galleriffic with the jquery.history plugin ****/

    // PageLoad function
    // This function is called when:
    // 1. after calling $.historyInit();
    // 2. after calling $.historyLoad();
    // 3. after pushing "Go Back" button of a browser
    function pageload(hash) {
	// alert("pageload: " + hash);
	// hash doesn't contain the first # character.
	if(hash) {
	    $.galleriffic.gotoImage(hash);
	} else {
	    gallery.gotoIndex(0);
	}
    }

    // Initialize history plugin.
    // The callback is called at once by present location.hash. 
    $.historyInit(pageload, "advanced.html");

    // set onlick event for buttons using the jQuery 1.3 live method
    $("a[rel='history']").live('click', function(e) {
	if (e.button != 0) return true;
	
	var hash = this.href;
	hash = hash.replace(/^.*#/, '');

	// moves to a new page. 
	// pageload is called at once. 
	// hash don't contain "#", "?"
	$.historyLoad(hash);

	return false;
    });
}

function populateSlideshow(jsonObj) {

    console.log(jsonObj);
    var totalBadges = 0;
    var newBadges = 0;
    var key, value, imgURL, title;

    for (key = 0; key < Object.size(jsonObj); key++) {
	console.log(key);
	console.log(jsonObj[key]);
	value = jsonObj[key];
	imgURL = fullURL(value.IconURL);
	title=value.Name;
	var HTML = 
	"<li>" +
	    "<a class=\"thumb\" name=\"leaf\" href=\""+imgURL+"\" title=\""+title+"\">" +
	        "<img class=\"badge\" src=\""+imgURL+"\" alt=\""+title+"\" /> " +
	    "</a>" +
	    "<div class=\"caption\">" +
	        "<div class=\"download\">" +
	            "<a href=\""+imgURL+"\">Download Original</a>" +
	        "</div>" +
	        "<div class=\"image-title\">"+title+"</div>" +
	    "</div>" +
	"</li>";
	document.getElementById('thumbs noscript').innerHTML += HTML;
    }
    enablePretty();
}

function loadHome(user, boss) {
    var paramObj = {
	email : user.email,
	bossEmail : boss.email,
	name : user.name,
	id : user.id
    };
    
    getBadgeList(paramObj, populateSlideshow);
}



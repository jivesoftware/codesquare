var glObj = {};
glObj.timer_is_on = 0;
glObj.refreshTime = 3000;
glObj.email = 'eric.ren@jivesoftware.com';
glObj.bossEmail = 'deanna.surma@jivesoftware.com';

//alert(glObj.timer_is_on); //test


//continually request data from server
function startBadges() {
    if (!glObj.timer_is_on) {
	glObj.timer_is_on = 1;
	alert("timer on: " + glObj.timer_is_on); //test
	getBadges();
	document.getElementById('button').innerHTML = "Stop requesting";
	
    } 
    else {
	glObj.timer_is_on = 0;
	alert("timer off: " + glObj.timer_is_on); //test
	clearTimeout(glObj.t);
	document.getElementById('button').innerHTML = "Continue requesting";
    }
}

// An utility function to get the size of an object
Object.size = function(obj) {
    var size = 0, key;
    for (key in obj) {
	if (obj.hasOwnProperty(key)) size++;
    }
    return size;
};

//calculates full url path, combines base URL and relativePath
function fullURL(relativePath) {
    var location = window.location.href;
    var index = location.indexOf("url=");
    //var index = -4;
    location = location.substring(index + 4);
    index = location.indexOf("&");
    if (index >= 0) {
	location = location.substring(0, index);
    }
    location = unescape(location.replace(/\+/g, " "));
    index = location.lastIndexOf("/");
    if (index >= 0) {
	location = location.substring(0, index);
    }
    return location + "/" + relativePath;
}


// Does what you think it does...
function makeBadgeTable() {
    var url = 'http://10.45.111.143:9090/CodeSquareServlet/FrontEndServlet?email=' + glObj.email + '&bossEmail=' + glObj.bossEmail;
    //document.getElementById('test').innerHTML += name +"<br />" + url;
    var params = {'href' : url, 'format' : 'json', 'authz' : 'none' };
       
    osapi.http.get(params).execute(function(response) {
	    console.log(response);
	    if (response.error) {
		alert("Error: " + response.error.message + "\nbetter debug it..."); // Deal with this...
	    }
	    else {
		// use the data 
		var jsonObj = response.content;
		console.log(jsonObj);
		/*		
    var jsonObj = {
	"1": {
	    "Name": "Commit 1 Badge",
	    "IconURL": "images/1.png",
	    "Description": "Made at least one commit",
	    "new": "false"
	},
	"2": {
	    "Name": "Commit 50 Badge",
	    "IconURL": "images/2.png",
	    "Description": "Made at least fifty commits",
	    "new": "true"
	},
	"3": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"4": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"5": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"6": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"7": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"8": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"9": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"10": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"11": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"12": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"13": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"14": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"15": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"16": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"17": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"18": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"19": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"20": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"21": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"22": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"23": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"24": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"25": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"26": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "true"
	},
	"27": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"28": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"29": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "false"
	},
	"30": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "new": "true"
	}
	};*/
    	var totalBadges = 0;
    	var newBadges = 0;
	var tableHTML = "";
		for (var key = 1; key <= Object.size(jsonObj); key++) {
		    var value = jsonObj[key];
		    if (key % 5 == 1) {
			 tableHTML += "<tr>";
		     }
		    var imgURL = fullURL(value.IconURL);
		     console.log(fullURL(imgURL));
		     var popupURL = fullURL("badgePopup.html");
		     console.log(imgURL);
		     tableHTML += "<td><form action='" + popupURL + "' method='GET'>" +
			     "<input type='hidden' name='imgURL' value='" + imgURL + "'/>" +
			     "<input type='hidden' name='name' value='" + value.Name + "'/>" +
			     "<input type='hidden' name='desc' value='" + value.Description + "'/>";
			if (value.Name !== "Unobtained") {totalBadges = totalBadges + 1;}
			if (value.new == "true") {
				tableHTML+= "<input class=\"highlight\" type='image' src='" + imgURL + "' value='Popup!' alt='Show badge info'/></form>";
				newBadges = newBadges + 1;
			}
			else {
			    tableHTML+= "<input class=\"shadow\" type='image' src='" + imgURL + "' value='Popup!' alt='Show badge info'/></form></td>";
			}
				
		     if (key % 5 == 0) {
			 tableHTML += "</tr>";
		     }
		}
	    document.getElementById('badgeTable').innerHTML = tableHTML;

	    var badgeCountHTML = "";
	    if (newBadges > 0) {
		badgeCountHTML += "You earned "+newBadges+" new badges! ";
	    } else {
		badgeCountHTML+="You currently have "+totalBadges+" badges.";
	    }

	    document.getElementById('numberOfBadges').innerHTML = badgeCountHTML;
	    }
	});
    //glObj.t = setTimeout("getBadges()", glObj.refreshTime);
}

/*
function onLoadHandler() {
    var tabs = new gadgets.TabSet(__MODULE_ID__, null, document.getElementById('tabs_div'));
    var params = {
	callback: changeSelectedTab
    };

    params.contentContainer = document.getElementById('friends');
    tabs.addTab('Friends', params);

    params.contentContainer = document.getElementById('messages');
    tabs.addTab('Messages', params);
}
*/

function callback(tabId) {
    var p = document.createElement("p");
    // Get selected tab
    var selectedTab = tabs.getSelectedTab();
    p.innerHTML = "This is dynamic content generated in callback for tab " + selectedTab.getName();
    document.getElementById(tabId).appendChild(p);
    makeBadgeTable();
}

//ditching adding tabs
//var tabs = new gadgets.TabSet();
function init() {
    /*    osapi.people.getViewer().execute(function(viewerData) {
	    if (!viewerData.error) {
		var viewerDiv = document.getElementById('current_user_id'),
		    viewerThumbnailImg = document.getElementById('viewerThumbnailImg');
		viewerDiv.innerHTML += viewerData.displayName;
		viewerThumbnailImg.attributes.getNamedItem("src").nodeValue=viewerData.thumbnailUrl;
		
		var user = viewerData.displayName;
		glObj.user = user.replace(/\s/, '_');
    */
    /*
    tabs.addTab("Two", {
	    contentContainer: document.getElementById("two_id")
    });

    tabs.addTab("Badges", {
	    contentContainer: document.getElementById("badgeTab"),
	    callback: callback
    });
    */
    makeBadgeTable();
		//gadgets.window.adjustHeight();
		//}
//	})
}

gadgets.util.registerOnLoadHandler(init);


//gadgets.window.adjustHeight();

//Diiv Scripts

//generates the array of the names of the Viewer's friends and creates an autocomplete text box using the names in nameArray
function autoCompleteText(){
	var nameArray = []; //Initializes a new empty array
	var personArray = osapi.people.getViewerFriends(); // An array of Person JSON objects
	var arrayLen = personArray.length;
	
	//Iterate through person objects and object the name of each person and add it to nameArray
	for(var i = 0;i < arrayLen; i++){ 
		var friendName = personArray[i].name; //obtain name string field from Person JSON object
		nameArray[i] = friendName; //add name string to nameArray
	}
	
	//Used in testing
	var availableTags = [
		"ActionScript",
		"AppleScript",
		"Asp",
		"BASIC",
		"C",
		"C++",
		"Clojure",
		"COBOL",
		"ColdFusion",
		"Erlang",
		"Fortran",
		"Groovy",
		"Haskell",
		"Java",
		"JavaScript",
		"Lisp",
		"Perl",
		"PHP",
		"Python",
		"Ruby",
		"Scala",
		"Scheme"
	];
	//End used in testing
	
	$( "#tags" ).autocomplete({
		//source: availableTags //used in testing
		source: nameArray
	});
}

//sends a Jive message post to the person with friendName as their name
function sendMessage(friendName){
	
}


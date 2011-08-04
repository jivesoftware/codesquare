var glObj = {};
glObj.timer_is_on = 0;
glObj.refreshTime = 3000;

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

// Does what you think it does...
function makeBadgeTable(userEmail, bossEmail) {
    var url = 'http://10.45.111.143:9090/CodeSquare/AppServlet?email=' + userEmail  + '&bossEmail=' + bossEmail;
    var params = {'href' : url, 'format' : 'json', 'authz' : 'none', 'nocache' : 'true' };

    osapi.http.get(params).execute(function(response) {
	console.log(response);
	if (response.error) {
	    alert("Error: " + response.error.message + "\nbetter debug it..."); // Deal with this...
	}
	else {
	    // use the data 
	    var jsonObj = response.content;
	    var totalBadges = 0;
    	    var newBadges = 0;
	    var tableHTML = "";
	    for (var key = 1; key <= Object.size(jsonObj); key++) {
		var value = jsonObj[key];
		if (key % 5 == 1) {
		    tableHTML += "<tr>";
		}
		var imgURL = fullURL(value.IconURL);
		var popupURL = fullURL("badgePopup.html");
		tableHTML += "<td><form class='badges' action='" + popupURL + "' method='GET'>" +
		    "<input type='hidden' name='imgURL' value='" + imgURL + "'/>" +
		    "<input type='hidden' name='name' value='" + value.Name + "'/>" +
		    "<input type='hidden' name='desc' value='" + value.Description + "'/>";
		if (value.IconURL !== "images/unobtained.png") {totalBadges = totalBadges + 1;}
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
}

function callback(tabId) {
    var p = document.createElement("p");
    // Get selected tab
    var selectedTab = tabs.getSelectedTab();
    p.innerHTML = "This is dynamic content generated in callback for tab " + selectedTab.getName();
    document.getElementById(tabId).appendChild(p);
    makeBadgeTable();
}

function loadFancy() {
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
	console.dir($('form').data('events'));

	return false;
    });
}



function init() {
    osapi.people.getViewer().execute(function(viewerBasicData) {
	if (!viewerBasicData.error) {
    	    var request = osapi.jive.core.users.get({id: viewerBasicData.id});
    	    request.execute(function(viewer) {
    		if (!viewer.error) {
    		    var request2 = viewer.data.manager.get();
    		    request2.execute(function(bossBasicData) {
    			if (!bossBasicData.error) {
    			    var request3 = osapi.jive.core.users.get({id: bossBasicData.data.id});
    			    request3.execute(function(boss) {
    				if (!boss.error) {
    				    var user2 = boss.data
    				    console.log("USEREMAIL: "+viewer.data.email);
    				    console.log("BOSSEMAIL: "+boss.data.email);
				    glObj.email = viewer.data.email;
				    glObj.bossEmail = boss.data.email;
				    
    				    makeBadgeTable(viewer.data.email, boss.data.email);
				    $("form.badges")
					.live('submit', function(e) {
					    //console.log(e.type);
					    $form = $(this);
					    $.fancybox({
						'href': $form.attr("action") + "?" + $form.serialize(),
						'transitionIn' : 'elastic',
						'transitionOut' : 'elastic',
						'autoScale' : false,
						'width': '40%',
						'height': '80%',
						'type': 'iframe',
					    });
					    e.preventDefault();
					    console.log('form was submitted');
					    return false;
					});

    				}
    			    });
    			}
    		    });
    		}
   	    });
    	}
    });
}

gadgets.util.registerOnLoadHandler(init);

//Begin Diiv code

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
    
    $( "#friendName" ).autocomplete({
	//source: availableTags //used in testing
	source: availableTags
    });
    //alert(nameArray.length);
}



//sends a Jive message post to the person with frName as their name -not working fix
function sendMessage(frName){
   
};


$(document).ready(function(){
    //var testdata = ["c++", "java", "php", "coldfusion", "javascript", "asp", "ruby"];
    
    
    var nameArray = [];
    
    osapi.people.getViewerFriends().execute(function(viewerFriends) {
    	if(!viewerFriends.error){
    		var friendArray = viewerFriends.list;
    		
    		for(var i = 0; i < friendArray.length;i++){
    			nameArray[i] = friendArray[i].displayName;
    			console.log("Content in array: " + nameArray[i]);
    		}
    		
    		
    		$("#update-entry").autocomplete(nameArray);
    	}
    })
    
});

gadgets.util.registerOnLoadHandler(function() {
	  var form = $('update-form');
	  form.onsubmit = function() {
	    var msg = $('update-entry').value();
		sendActivityPost(msg);
	    return false;
	 };
	});

function sendActivityPost(msg){
	
	var params = {userId: "@viewer",
	              activity: {title:"Basic Acitivity Post",
	                         body: msg}
	             };
	osapi.activities.create(params).execute(diivCallback);
};

function diivCallback(){
        alert("You posted to their Activity Stream!");
};
	

//End Diivcode


function viewShare() {
    
    osapi.people.getViewerFriends().execute(function(viewerFriends) {
    var tableHTML="<table>"; 
        if (!viewerFriends.error){
            jQuery.each(viewerFriends.list, function() {
                tableHTML+="<tr> <td> <img src='"+this.thumbnailUrl+"' /> </td> <td> <a href=\"javascript:viewFollowing('"+this.id+"')\">"+this.displayName+"</a> </td> </tr>";
                console.log(this);
                  // tableHTML+=this.id;
            });
            console.log("2"+tableHTML);
        }
        tableHTML+="</table>";
        
        document.getElementById('vertTabs').innerHTML = tableHTML;
    });
   
}







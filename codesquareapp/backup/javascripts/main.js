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
// Gets json object from our Frontend servlet
// not going to use this
/*
  function getBadgeList(userEmail, bossEmail) {
  var url = 'http://10.45.111.143:9090/CodeSquare/AppServlet?email=' + userEmail  + '&bossEmail=' + bossEmail;
  var params = {'href' : url, 'format' : 'json', 'authz' : 'none', 'nocache' : 'true' };
  var resp;
  
  osapi.http.get(params).execute(function(response) {
  console.log(response);
  resp = response;
  });

  if (resp.error) {
  alert("Error: " + resp.error.message + "\nbetter debug it..."); // Deal with this..
  }
  else {
  return resp.content;
  }
  }
*/

// Does what you think it does...
function makeBadgeTable(userEmail, bossEmail) {
    var url = 'http://10.45.111.143:9090/CodeSquare/AppServlet?email=' + userEmail  + '&bossEmail=' + bossEmail;
    var params = {'href' : url, 'format' : 'json', 'authz' : 'none', 'nocache' : 'true' };
    
    osapi.http.get(params).execute(function(response) {
	if (response.error) {
	    alert("Error: " + resp.error.message + "\nbetter debug it..."); // Deal with this..
	}
	else {
	    var jsonObj = response.content;
	    console.log(jsonObj);
	    var totalBadges = 0;
	    var newBadges = 0;
	    var tableHTML = "";
	    var stateClass = "shadow";
	    var key, value, imgURL, popupURl;
	    
	    for (key = 1; key <= Object.size(jsonObj); key++) {
		value = jsonObj[key];
		if (key % 5 == 1) {
		    tableHTML += "<tr>";
		}
		imgURL = fullURL(value.IconURL);
		popupURL = fullURL("badgePopup.html");
		tableHTML += "<td><form class='badges' action='" + popupURL + "' method='GET'>" +
		    "<input type='hidden' name='imgURL' value='" + imgURL + "'/>" +
		    "<input type='hidden' name='name' value='" + value.Name + "'/>" +
		    "<input type='hidden' name='desc' value='" + value.Description + "'/>";
		if (value.IconURL !== "images/unobtained.png") {
		    totalBadges = totalBadges + 1;
		}
		console.log('value.New is ' + value.New);
		if (value.New == "true") {
		    stateClass = "highlight";
		    newBadges = newBadges + 1;
		    console.log(stateClass);
		}
		console.log('stateClass is ' + stateClass);
		tableHTML+= "<input class='" + stateClass + "' type='image' src='" + imgURL + "' value='Popup!' alt='Show badge info'/></form></td>";
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

// this function is not needed since we're not using gadget tabs
function callback(tabId) {
    var p = document.createElement("p");
    // Get selected tab
    var selectedTab = tabs.getSelectedTab();
    p.innerHTML = "This is dynamic content generated in callback for tab " + selectedTab.getName();
    document.getElementById(tabId).appendChild(p);
    makeBadgeTable();
}

// this function is not being used right now
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
gadgets.window.adjustHeight();



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







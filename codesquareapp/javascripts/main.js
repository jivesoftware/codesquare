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
    var url = 'http://10.45.111.143:9090/CodeSquareServlet/FrontEndServlet?email=' + userEmail + '&bossEmail=' + bossEmail;
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
}

function callback(tabId) {
    var p = document.createElement("p");
    // Get selected tab
    var selectedTab = tabs.getSelectedTab();
    p.innerHTML = "This is dynamic content generated in callback for tab " + selectedTab.getName();
    document.getElementById(tabId).appendChild(p);
    makeBadgeTable();
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
    								makeBadgeTable(viewer.data.email, boss.data.email);
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



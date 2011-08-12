var GLOBJ = {};


// this function is used to load the badge tables in the 'Browse Badges' tab and the 'Compare' tab
function makeBadgeTable(userEmail) {
    
    var args = arguments;
    var url = 'http://10.45.111.143:9090/CodeSquare/AppServlet?email=' + userEmail;
    
    // if we're passing in 2 arguments, then we're making requests from the compare tab 
    if (args.length === 2) {
	url = url + "&compare=true";
    }

    var params = {'href' : url, 'format' : 'json', 'authz' : 'none', 'nocache' : 'true' };
    
    osapi.http.get(params).execute(function(response) {
	if (response.error) {
	    alert("Error: " + resp.error.message + "\nbetter debug it..."); // Deal with this..
	}
	else {
	    var jsonObj = response.content;
	    //console.log(jsonObj);
	    var totalBadges = 0;
	    var newBadges = 0;
	    var tableHTML = "";
	    var stateClass = "shadow";
	    var key, value, imgURL, thumbnailUrl, popupURl;
	    
	    for (key = 1; key <= Object.size(jsonObj); key++) {
		value = jsonObj[key];
		if (key % 5 === 1) {
		    tableHTML += "<tr>";
		}
		imgURL = fullURL(value.IconURL);
		thumbnailUrl = fullURL(value.thumbnail);
		//console.log(value.thumbnail);
		//console.log(thumbnailUrl);
		popupURL = fullURL("badgePopup.html");
		tableHTML += "<td><form class='badges' action='" + popupURL + "' method='GET'>" +
		    "<input type='hidden' name='imgURL' value='" + imgURL + "'/>" +
		    "<input type='hidden' name='name' value='" + value.Name + "'/>" +
		    "<input type='hidden' name='desc' value='" + value.Description + "'/>";
		if (value.IconURL !== "images/unobtained.png") {
		    totalBadges = totalBadges + 1;
		}
		//console.log('value.New is ' + value.New);
		if (value.New == "true") {
		    stateClass = "highlight";
		    newBadges = newBadges + 1;
		//    console.log(stateClass);
		}
		//console.log('stateClass is ' + stateClass);
		tableHTML+= "<input class='" + stateClass + "' type='image' src='" + thumbnailUrl + "' value='Popup!' alt='Show badge info'/></form></td>";
		if (key % 5 === 0) {
		    tableHTML += "</tr>";
		}
	    }

	    document.getElementById('badgeTable').innerHTML = tableHTML;

	    var badgeCountHTML = "";
	    
	    // this snippet of code is used for loading friend's name in compare tab
	    if (args.length === 2) {
		console.log('here is the arguments object');
		console.log(args);
		// assume that arguments[1] contains the name of the friend
	    	badgeCountHTML+=args[1]+" has "+totalBadges+" badges.";
	    } else {
	    	if (newBadges > 0) {
		    badgeCountHTML += "You earned "+newBadges+" new badges! ";
	    	} 
		badgeCountHTML += "You currently have "+totalBadges+" badges.";
	    }
	    document.getElementById('numberOfBadges').innerHTML = badgeCountHTML;
	    gadgets.window.adjustHeight();
	}
    });    
}

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

function init() {
    try {
	osapi.jive.core.users.get({id: '@viewer'}).execute(function(userResponse) {
    	    if (!userResponse.error) {
		var user = userResponse.data;
		//console.log(user);
		var basicBossRequest = user.manager.get();
    		basicBossRequest.execute(function(basicBossResponse) {
    		    if (!basicBossResponse.error) {
			var basicBoss = basicBossResponse.data;
			//console.log(basicBoss);
    			osapi.jive.core.users.get({id: basicBoss.id}).execute(function(bossResponse) {
    			    if (!bossResponse.error) {
    				var boss = bossResponse.data
				//console.log(boss);
    				//console.log("USEREMAIL: "+user.email);
    				//console.log("BOSSEMAIL: "+boss.email);
			    	
				// setting up some global vars, this should be the only place to set these evil variables
				GLOBJ.name = user.name;
				GLOBJ.email = user.email;
				GLOBJ.bossEmail = boss.email;
				GLOBJ.id = user.id;
				/*console.log(GLOBJ.name);
				  console.log(GLOBJ.email);
				  console.log(GLOBJ.bossEmail);
				  console.log(GLOBJ.id);
				*/
    				makeBadgeTable(user.email);
				
				// JQuery event binding for badge popups
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
					    'height': '60%',
					    'type': 'iframe',
					});
					//$.fancybox.resize();
					e.preventDefault();
					console.log('form was submitted');
					return false;
				    });
				
    			    } else {
				throw {name : 'boss response error', message :  bossResponse.error.message};
    			    }
			});
    		    } else {
			throw {name : 'basic boss response error', message :  bossBasicData.error.message};
    		    }
		});
    	    } else {
		throw {name : 'user response error', message : userResponse.error.message};
	    }
	});
    } catch (e) {
	console.log(e.name);
	console.log(e.message);
    }
}

gadgets.util.registerOnLoadHandler(init);
gadgets.window.adjustHeight();










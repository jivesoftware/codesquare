// This GLOBAL object should ONLY contain the user data for a current session ( name, email ,bossEmail, id).
// Once created and modfied by the loadCanvas() function, the object should never change during the session.
var GLOBAL = {
    AppServletURL : 'http://10.45.111.143:9090/CodeSquare/AppServlet'
};


// A utility function to get the size of an object
Object.size = function(obj) {
    var size = 0, key;
    for (key in obj) {
	if (obj.hasOwnProperty(key)) size++;
    }
    return size;
};

// init function gets called when either 'home' or 'canvas' view 
function init(callback) {
    try {
	osapi.jive.core.users.get({id: '@viewer'}).execute(function(userResponse) {
    	    if (!userResponse.error) {
		var user = userResponse.data;
		var basicBossRequest = user.manager.get();
    		basicBossRequest.execute(function(basicBossResponse) {
    		    if (!basicBossResponse.error) {
			var basicBoss = basicBossResponse.data;
    			osapi.jive.core.users.get({id: basicBoss.id}).execute(function(bossResponse) {
    			    if (!bossResponse.error) {
    				var boss = bossResponse.data
				callback(user, boss);
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

// links for tabs
function changePage(page) {
    var params = {'href' : fullURL(page), 'authz' : 'none' };
    osapi.http.get(params).execute(function(response) {
	console.log(response);
	document.getElementById("main-wrapper").innerHTML=response.content;
    });
}

// gets the correct badge info from appservlet depending on which args are passed, and passes that json response to processor function
function getBadgeList(paramObj, processor) {
    var url = GLOBAL.AppServletURL + '?email=' + paramObj.email;

    // if we are passing in property 'compare', then must be a servlet request from the compare tab
    if (paramObj.compare) {
	url += '&compare=true';
    }
    
    // if passing in bossEmail, must be a request from the home view
    if (paramObj.bossEmail) {
	url += '&bossEmail=' + paramObj.bossEmail + '&name=' + encodeURI(paramObj.name) + '&id=' + paramObj.id;
    }

    var httpParams = {'href' : url, 'format' : 'json', 'authz' : 'none', 'nocache' : 'true' };

    osapi.http.get(httpParams).execute(function(response) {
	if (response.error) {
	    console.log("Error: " + response.error.message + "\nbetter debug it...");
	} else {
	    processor(response.content, paramObj.friendName);
	}
    });
}


// used for autocomplete feature for brag tab
function enableAutoComplete() {
    //var testdata = ["c++", "java", "php", "coldfusion", "javascript", "asp", "ruby"];

    var nameArray = [];

    osapi.people.getViewerFriends().execute(function(viewerFriends) {
	if(!viewerFriends.error){
	    var friendArray = viewerFriends.list;

	    for(var i = 0; i < friendArray.length;i++){
	    	nameArray[i] = friendArray[i].displayName;
	    	console.log("Content in array: " + nameArray[i]);
	    }


	    $("#update-entry1").autocomplete(nameArray);

	    var oldName = $("#update-entry1").val();


	    $("#update-entry1").blur(function() {
		var newName = $("#update-entry1").val();

		if(oldName != newName){
		    console.log("Entered update-entry2 anonymous function");

		    var index = nameArray.indexOf($("#update-entry1").val());


		    if(index != -1){

			console.log("Name: " + $("#update-entry1").val());

			index = nameArray.indexOf($("#update-entry1").val());
			console.log("Index: " + index);

			var imageURL = friendArray[index].thumbnailUrl;
			console.log("Image URL: " + imageURL);
			var imageCallString = "<img src='" + imageURL + "' />";
			var callString = "<style='padding:5px;'>" + imageCallString + "</style>"; 
			$('#pictureDisplay').html(callString);

		    }

		}
	    });

	    

	    $('#update-form').bind('keypress', function(e) {
	        if(e.keyCode==13){
	            
		    return;
	        }
	    });
	    
	    

	}
    });
}
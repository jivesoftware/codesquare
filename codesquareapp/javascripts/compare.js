// wrapper for makeBadgeTable to run based on userId
function makeBadgeTableFromId(userId) { 
    var request = osapi.jive.core.users.get({id: userId});
    request.execute(function(viewer) {
    	if (!viewer.error) {
    	    makeBadgeTable(viewer.data.email, viewer.data.firstName);
    	} else {
	    console.log("Error in makeBadgeTableFromId(). Examine error object below");
	    console.log(viewer.error);
	}
    }); 
}


// compare's main function
function getFollowersToCompare() {
    osapi.people.getViewerFriends().execute(function(viewerFriends) {
        if (!viewerFriends.error){
            
            for(var k = 0; k < (viewerFriends.list.length);k++){
        	console.log("<span id='"+k+"'> </span>");
        	document.getElementById('lsidebar-wrapper').innerHTML+="<span id='"+k+"'> </span>";
            }
            
	    var friendList = viewerFriends.list.sort(function (a,b) {
		aName = a.displayName.toLowerCase();
		bName = b.displayName.toLowerCase();
		if (aName < bName) { return -1; }
		if (aName > bName) { return 1; }
		if (aName == bName) { return 0; }
	    });
	    
            var tableHTML="<div id='listOfFriends'> <form> <select>\"";
            for(var i = 0; i < friendList.length;i++){
        	tableHTML+="<option onclick=\"javascript:makeBadgeTableFromId('"+friendList[i].id+"')\">"+friendList[i].displayName+"</option>";
            }
            document.getElementById('lsidebar-wrapper').innerHTML=tableHTML+"</select> </form> <br/> <ul style=\"padding-left: 0px;\">"+document.getElementById('lsidebar-wrapper').innerHTML;
            
            var count = 0;
            for(var j = 0; j < (friendList.length);j++){
        	console.log("1: "+count);
        	osapi.jive.core.users.get({id: friendList[j].id}).execute(function(user) {
        	    console.log("2: "+count);
        	    if (!user.error) {
    			var nameLength = (user.firstName+"&nbsp;"+user.lastName).length;
    			console.log("3: "+count);
    			console.log(document.getElementById(count).innerHTML);
    			if (nameLength > 25) {
    			    document.getElementById(count).innerHTML+="<img src='"+user.data.avatarURL+"' onclick=\"javascript:makeBadgeTable('"+user.data.email+"','"+user.data.firstName+"')\" />&nbsp;<a href=\"javascript:makeBadgeTable('"+user.data.email+"','"+user.data.firstName+"')\">"+user.data.firstName+"&nbsp;"+user.data.lastName.charAt(0)+".</a> <br/>";
                	}
    			else {
    			    document.getElementById(count).innerHTML+="<img src='"+user.data.avatarURL+"' onclick=\"javascript:makeBadgeTable('"+user.data.email+"','"+user.data.firstName+"')\" />&nbsp;<a href=\"javascript:makeBadgeTable('"+user.data.email+"','"+user.data.firstName+"')\">"+user.data.firstName+"&nbsp;"+user.data.lastName+"</a> <br/>";
                	}
                	count = count+1;
    		    }
    		});
            }
        }
    });
}


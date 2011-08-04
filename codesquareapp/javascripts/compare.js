function getFollowersToCompare() {
    osapi.people.getViewerFriends().execute(function(viewerFriends) {
        if (!viewerFriends.error){
        	var tableHTML="<div id='listOfFriends'> <ul>"; 
        	tableHTML+="<form id=\"update-form\" method=\"GET\" class=\"ui-corner-all\">";
				tableHTML+="<p style=\"padding: 4px;\">";
					tableHTML+="<input id=\"update-entry1\" type=\"text\" />";
					tableHTML+="<div id=\"pictureDisplay\">";
		    		tableHTML+="</div>";
		    	tableHTML+="</p>";
			tableHTML+="</form>";
		
        	var len = viewerFriends.list.length;
            jQuery.each(viewerFriends.list, function(index, value) {
    	    	osapi.jive.core.users.get({id: value.id}).execute(function(user) {
    				if (!user.error) {
    					var nameLength = (user.data.firstName+"&nbsp;"+user.data.lastName).length;
    					if (nameLength > 20) {
    					tableHTML=tableHTML+"<p> <img src='"+value.thumbnailUrl+"' />&nbsp;<a href=\"javascript:makeBadgeTable('"+user.data.email+"', '',false,'"+user.data.firstName+"')\">"+user.data.firstName+"&nbsp;"+user.data.lastName.charAt(0)+".</a> </p>";
                		}
    					else {
    					tableHTML=tableHTML+"<p> <img src='"+value.thumbnailUrl+"' />&nbsp;<a href=\"javascript:makeBadgeTable('"+user.data.email+"', '',false,'"+user.data.firstName+"')\">"+user.data.firstName+"&nbsp;"+user.data.lastName+"</a> </p>";
                		}
                	}
                	if (index == len - 1) {
                	document.getElementById('lsidebar-wrapper').innerHTML = tableHTML+"</ul></div>";
                	}
            	});
        	});
        }
    });
    
}
//Diiv code

function handleForm(){
    console.log("Entering handleForm");
    var friendName = $('#update-entry1').val();
    var message = $('#update-entry2').val();
    console.log("update-entry1: " + friendName);
    console.log("update-entry2: " + message);
	
	
    sendActivityPost(friendName, message);
};

function sendActivityPost(friendName, message){
	 console.log("Entering sendActivityPost");
	
	


    osapi.people.getViewerFriends().execute(function(viewerFriends){
	if(!viewerFriends.error){
	    var friendArray = viewerFriends.list;
	    
	    for(var i = 0;i < friendArray.length;i++){
		if(friendArray[i].displayName == friendName){
		    var frid = friendArray[i].id;
		    osapi.people.getViewer().execute(function (viewerData){
			if(!viewerData.error){
			    var viewerName = viewerData.displayName;
			    var viewerID = viewerData.id;
			    console.log("FriendName: " + friendName);
			    console.log("Message: " + message);
			    console.log("FriendID: " + frid);
			    console.log("ViewerName: " + viewerName);
			    console.log("ViewerID:" + viewerID);
			    var msg2 = "Hi " +  friendName + "! You've got a CodeSquare Badge Post from " + viewerName + "!";
			    var idString = "urn:jiveObject:user/" + frid;
			    var myidString = "urn:jiveObject:user/" + viewerID;
				var badgePicURL = $("#badgeSelect").val();
				console.log("badgePicURL: " + badgePicURL);
			    var activity = {"activity":{"body": message, "verb": "post", 
							"object" : {
							    "objectType":"article",
							    "summary": message,
							    "mediaLink": { 
								"mediaType": "photo", 
								"url" : badgePicURL
							    }
							},
							
						       },
					    "target": {
						"id": myidString,
						"displayName":friendName
					    }
					   }
			    
			    var params = {"activity": {"title": "Go to your What Matters: Activities to check it out!",
						       "body": msg2
						      },
					  "deliverTo": frid
					 };
			    var msg3 = "Hi " + viewerName + ", you sent this message to " + friendName + " from your CodeSquare App!";
			    var params2 = {"activity": {"title": message,
							"body": msg3
						       },
					   "deliverTo": viewerID
					  };
			    console.log("At User: " + "@user");
			    osapi.activities.create(activity).execute(streamPostCallback(message));
			    osapi.activities.create(params).execute(notificationPostCallback(message));
			    osapi.activities.create(params2).execute(userCallback(message));
			}
		    });
		    console.log("Done posting, exiting sendActivityPost");	
		    break;
		}
	    }
	}
    });
};

function streamPostCallback(message){
    console.log("Entering streamPostCallback");
    alert("You posted to their Activity Stream! He/She will be notified of this!");
};

function notificationPostCallback(message){
    console.log("Entering notificationPostCallback");
};

function userCallback(message){
    console.log("Entering userCallback");  
};	

function makeBadgeSelectionGallery(userEmail,bossEmail){
	console.log("Inside u MBSG: " + userEmail);
	console.log("Inside b MBSG: " + bossEmail);
	
	var url = 'http://10.45.111.143:9090/CodeSquare/AppServlet?email=' + userEmail  + '&bossEmail=' + bossEmail;
    var params = {'href' : url, 'format' : 'json', 'authz' : 'none', 'nocache' : 'true' };

	osapi.http.get(params).execute(function (response){
		if(response.error){
			alert("Invalid response: Unable to load badges");
		}else{
			var bjson = response.content; //bjson = json of badges
			var imageArray = [];
			var i = 0;
			for (var key = 1; key <= Object.size(bjson); key++){
				var badge = bjson[key];
				var badgePic = fullURL(badge.IconURL);
				
				if(badge.IconURL !== "images/unobtained.png"){
					imageArray[i++] = badgePic;
				}
				
				
			} 
			console.log("Images in imageArray: " + imageArray);
			if(imageArray.length > 0){
				fillUpImageDiv(imageArray);
			}else{
				$("#picBadgeSelect").html("<p>You have no Badges! You need to earn 'em before you can share 'em!</p>");
			}
			
		}
	});
}

function fillUpImageDiv(imageArray){
	console.log("Entered fillUpImageDiv");
	console.log("Contents in image array: " + imageArray);
	var fullImageHTML = "";
	for(var i = 0; i < imageArray.length; i++){
		//var imageHTMLWrap = "<div class=\"wrapper\" href=\"" + imageArray[i] + "\">";
		if(i % 5 == 0){
			fullImageHTML += '<br />'
		}
		var imageHTML = "<img class=\"picBadge\" width=\"100\" height=\"100\" src=\"" + imageArray[i] + "\" \/>";
		fullImageHTML += imageHTML;
	}
	console.log("HTML being appended: " + fullImageHTML);
	$("#picBadgeSelect").html(fullImageHTML);
	$(".picBadge").click(function(){
		console.log("Info of this clicked pic: " + this.src);
		var selImgURL = this.src;
		var selHTML = '<br /><img width="100" height="100" src="' + selImgURL + '" />';
		$("#selectedBadge").html(selHTML);
		$("#badgeSelect").val(selImgURL);
		console.log("Value in badgeSelect form input: " + $("#badgeSelect").val());
	});
}

function bragBasics() {
	nicEditors.allTextAreas();
	
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
						makeBadgeSelectionGallery(viewer.data.email, boss.data.email);
    				}
    			    });
    			}
    		    });
    		}
   	    });
    	}
    });
}
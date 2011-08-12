// A utility function to get the size of an object
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

// links for tabs
function changePage(page) {
	var params = {'href' : fullURL(page), 'authz' : 'none' };
	osapi.http.get(params).execute(function(response) {
		console.log(response);
		document.getElementById("main-wrapper").innerHTML=response.content;
	});
}



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
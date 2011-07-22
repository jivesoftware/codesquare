function makeCarousel(userEmail, bossEmail, carousel, state) {
	var url = 'http://10.45.111.143:9090/CodeSquareServlet/FrontEndServlet?email='+userEmail+'&bossEmail='+bossEmail;
    var params = {'href' : url, 'format' : 'json', 'authz' : 'none' };
	osapi.http.get(params).execute(function(response) {
		if (response.error) {
			alert("Error: " + response.error.message + "\nbetter debug it..."); }
	    else {
			mycarousel_itemAddCallback(carousel, carousel.first, carousel.last, response.content); }
	});
}

function mycarousel_itemLoadCallback(carousel, state) {
    if (state != 'init') return;

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
    								makeCarousel(viewer.data.email, boss.data.email, carousel, state);
    							}
    						});
    					}
					});
    			}
   			});
    	}
	});  
};

function mycarousel_itemAddCallback(carousel, first, last, data) {
	var count = 0;
	for (var key = 1; key <= (Object.size(data)); key++) {
		    var value = data[key];
			if (value.IconURL !== 'images/unobtained.png') {
				count = count+1;
 	 			carousel.add(count, mycarousel_getItemHTML(fullURL(value.IconURL), value.Name, value.Description)); 
 	 		}
 	 }
    insertCount(count);
    carousel.size(count);
};

/**
 * Item html creation helper.
 */
function mycarousel_getItemHTML(url, name, description)
{ 
    return '<table width="293" height="110">' +
    			'<tr>' + 
    				'<td> <img class="badge" src="' + url + '" alt="'+url+'" /> </td>' +
    				'<td> <p> <h4>'+name+'</h4>'+description+' </p> </td>' +
    			'</tr>' +
    		'</table>';
};

jQuery(document).ready(function() {
    jQuery('#mycarousel').jcarousel({
        itemLoadCallback: mycarousel_itemLoadCallback
    });
});

function insertCount(count) { document.getElementById("count").innerHTML = "You have "+count+" badges";}

Object.size = function(obj) {
    var size = 0, key;
    for (key in obj) {
	if (obj.hasOwnProperty(key)) size++;
    }
    return size;
};

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

var themeAry = [ 'Commits', 'Holidays', 'Time/Date', 'Viral Vids', 'Boss and Peers', 'Basic'];
var glObj = {};
glObj.timer_is_on = 0;
glObj.refreshTime = 3000;
//alert(glObj.timer_is_on); //test


// Get the size of an object
Object.size = function(obj) {
    var size = 0, key;
    for (key in obj) {
	if (obj.hasOwnProperty(key)) size++;
    }
    return size;
};


//continually request data from server
function startBadges() {
    if (!glObj.timer_is_on) {
	glObj.timer_is_on = 1;
	//	alert("timer on: " + glObj.timer_is_on); //test
	getBadges();
	document.getElementById('button').innerHTML = "Stop requesting";
	
    } 
    else {
	glObj.timer_is_on = 0;
	//	alert("timer off: " + glObj.timer_is_on); //test
	clearTimeout(glObj.t);
	document.getElementById('button').innerHTML = "Continue requesting";
    }
}

//change for testing
function makeBadgeTable() {
    //    var url = 'http://10.45.120.176:8080/CodeSquare/FrontEndServlet?email=' + glObj.name;
    var url = 'http://10.45.120.176:8080/CodeSquare/FrontEndServlet?email=diivanand.ramalingam@jivesoftware.com';
    //document.getElementById('test').innerHTML += name +"<br />" + url;
    var params = {'href' : url, 'format' : 'json', 'authz' : 'none' };
    
    osapi.http.get(params).execute(function(response) {
	    if (response.error) {
		alert("Error: " + response.error.message + "\nbetter debug it..."); // Deal with this...
	    }
	    else {
		// get json data from response
		var obj = response.content;
		var key, value, 
		    themeNum = 0,
		    html = "";

		//1. 6. 11
		//sort the json data
		for (key = 1; key <= Object.size(obj); key++) {
		     value = obj[key];
		     console.log(value);
		     if (key % 5 == 1) {
			 html += "<tr><th>" + themeAry[themeNum] + "</th>";
			 themeNum++;
		     }
		     html += "<td><img src=" + value.IconURL + "/><p>" + value.Name + "</p></td>";
		     if (key % 5 == 0) {
			 html += "</tr>";
		     }
		}
	    }
	    
	    document.getElementById('badgeTable').innerHTML = html;
	    //glObj.t = setTimeout("getBadges()", glObj.refreshTime);
	});
}

function init() {
    osapi.people.getViewer().execute(function(viewerData) {
	    if (!viewerData.error) {
		var viewerDiv = document.getElementById('current_user_id'),
		    viewerThumbnailImg = document.getElementById('viewerThumbnailImg');
		viewerDiv.innerHTML += viewerData.displayName;
		viewerThumbnailImg.attributes.getNamedItem("src").nodeValue=viewerData.thumbnailUrl;
		
		var user = viewerData.displayName;
		glObj.user = user.replace(/\s/, '_');
		
		//gadgets.window.adjustHeight();
	    }
	})
}

gadgets.util.registerOnLoadHandler(init);
//gadgets.window.adjustHeight();


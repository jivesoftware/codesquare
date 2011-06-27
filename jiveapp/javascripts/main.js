
var glObj = {};
glObj.timer_is_on = 0;
glObj.refreshTime = 3000;
alert(glObj.timer_is_on); //test


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

function getBadges() {
    var url = 'http://10.45.120.176:8080/CodeSquare/FrontEndServlet?name=' + glObj.name;
    //document.getElementById('test').innerHTML += name +"<br />" + url;
    var params = {'href' : url, 'format' : 'json', 'authz' : 'none' };
    
    osapi.http.get(params).execute(function(response) {
	    if (response.error) {
		alert("Error: " + response.error.message + "\nbetter debug it..."); // Deal with this...
	    }
	    else {
		// use the data 
		var jsondata = response.content;
		var html = "";
		for (var key in jsondata) {
		    var value = jsondata[key];
		    html += key + ": " + value + "<br />";
		}
	    }
	    
	    document.getElementById('content_div').innerHTML = html;
	    glObj.t = setTimeout("getBadges()", glObj.refreshTime);
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
		
		gadgets.window.adjustHeight();
	    }
	})
}

gadgets.util.registerOnLoadHandler(init);
gadgets.window.adjustHeight();


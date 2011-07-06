var glObj = {};
glObj.timer_is_on = 0;
glObj.refreshTime = 3000;
glObj.themeAry = [ 'Commits', 'Holidays', 'Time/Date', 'Viral Vids', 'Boss and Peers', 'Basic'];
glObj.baseURL = "http://apphosting.jivesoftware.com/apps/dev/codesquareapp/";

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
    //var url = 'http://10.45.120.176:8080/CodeSquare/FrontEndServlet?email=diivanand.ramalingam@jivesoftware.com';
    /*
    var params = {'href' : url, 'format' : 'json', 'authz' : 'none' };

    osapi.http.get(params).execute(function(response) {
	    if (response.error) {
		alert("Error: " + response.error.message + "\nbetter debug it..."); // Deal with this...
	    }
	    else {
		// get json data from response
		var obj = response.content;
    */		var key, value, 
		    themeNum = 0,
		    html = "";

    var obj = {
	"1": {
	    "Name": "Commit 1 Badge",
	    "IconURL": "images/1.png",
	    "Description": "Made at least one commit",
	    "CustomMsg": ""
	},
	"2": {
	    "Name": "Commit 50 Badge",
	    "IconURL": "images/2.png",
	    "Description": "Made at least fifty commits",
	    "CustomMsg": ""
	},
	"3": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"4": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"5": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"6": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"7": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"8": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"9": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"10": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"11": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"12": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"13": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"14": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"15": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"16": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"17": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"18": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"19": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"20": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"21": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"22": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"23": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"24": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"25": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"26": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"27": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"28": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"29": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	},
	"30": {
	    "Name": "Unobtained",
	    "IconURL": "images/unobtained.png",
	    "Description": "Click to learn how to obtain",
	    "CustomMsg": ""
	}
    };
    console.log(obj);
		//1. 6. 11
		//sort the json data
		for (key = 1; key <= Object.size(obj); key++) {
		     value = obj[key];
		     //console.log(value);
		     if (key % 5 == 1) {
			 html += "<tr><th>" + glObj.themeAry[themeNum] + "</th>";
			 themeNum++;
		     }

		     var imgURL = glObj.baseURL + value.IconURL;
		     html += "<td><a class='extLink' href='" + glObj.baseURL + "popup.html?imgURL=" + imgURL + "&name=" + value.Name + "&desc=" + value.Description + "'>" +
			 "<img src='" + imgURL + "' /></a><p>" + value.Name + "</p></td>";

		     if (key % 5 == 0) {
			 html += "</tr>";
		     }
		}
	    
	    console.log(html);
	    //	    alert(document.getElementById('badgeTable').innerHTML);
	    document.getElementById('badgeTable').innerHTML = html;
	    //	    alert(document.getElementById('badgeTable').innerHTML);
	    //glObj.t = setTimeout("getBadges()", glObj.refreshTime);
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
		
		makeBadgeTable();

		//gadgets.window.adjustHeight();
	    }
	})
}

gadgets.util.registerOnLoadHandler(init);
gadgets.window.adjustHeight();


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
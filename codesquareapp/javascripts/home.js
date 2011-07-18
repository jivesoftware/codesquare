var count = 0;
function mycarousel_itemLoadCallback(carousel, state)
{
    // Since we get all URLs in one file, we simply add all items
    // at once and set the size accordingly.
    if (state != 'init')
        return;

// THIS IS WHERE YOU GET THE JSON INFO

// doesn't work due to crossbrowser issues - will work when transfered to jiveapp api
// just gives alert to show it's null.
//    $.getJSON('http://10.45.111.143:9090/CodeSquareServlet/FrontEndServlet?email=eric.ren@jivesoftware.com', null, function(data) {
//	alert(data);
//    });


    var url = 'http://10.45.111.143:9090/CodeSquareServlet/FrontEndServlet?email=eric.renm@jivesoftware.com&bossEmail=deanna.surma@jivesoftware.com';
    var params = {'href' : url, 'format' : 'json', 'authz' : 'none' };

    osapi.http.get(params).execute(function(response) {
	    if (response.error) {
		alert("Error: " + response.error.message + "\nbetter debug it..."); // Deal with this...
	    }
	    else {
		// get json data from response
		var data = response.content;
		console.log(data);
		mycarousel_itemAddCallback(carousel, carousel.first, carousel.last, data);
	    }
	});
  
// code that actually runs everything - use and change this to jive api (not above code)
//    jQuery.get('http://10.45.111.143:9090/CodeSquareServlet/FrontEndServlet?email=eric.ren@jivesoftware.com', function(data) {
//       console.log(data);
//       mycarousel_itemAddCallback(carousel, carousel.first, carousel.last, data);

//    });
    
};

function mycarousel_itemAddCallback(carousel, first, last, data)
{
    // Simply add all items at once and set the size accordingly.
    
	var x = jQuery.parseJSON(data);
	count = 0;

	jQuery.each(x, function(i) {
		if (x[i].Name !== 'Unobtained') {
			count = count+1;
 	 		carousel.add(count, mycarousel_getItemHTML(x[i].IconURL, x[i].Name, x[i].Description)); 
 	 	}
 	 });
// CHANGE - COUNT ISSUE!!!
    carousel.size(count);
};

/**
 * Item html creation helper.
 */
function mycarousel_getItemHTML(url, name, description)
{ 
    return '<table width="293" height="110">' +
    			'<tr>' + 
    				'<td>'+
    				'<img class="badge" src="' + url + '" alt="'+url+'" /> </td>' +
    				'<td><h4>'+name+'</h4>'+description+'</td>' +
    			'</tr>' +
    		'</table>';
};

jQuery(document).ready(function() {
    jQuery('#mycarousel').jcarousel({
        itemLoadCallback: mycarousel_itemLoadCallback
    });
});

function insertCount () { document.getElementById("count").innerHTML = count;}


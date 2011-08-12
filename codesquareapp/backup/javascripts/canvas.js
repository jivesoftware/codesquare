$('#browse a').click(function(e) {
    changePage('subpages/browse.html');
    makeBadgeTable(glObj.email,glObj.bossEmail,true,"");
    document.getElementById('lsidebar-wrapper').innerHTML="";
    tab = $('#browse');
    tab.addClass('current-cat');
    tab.siblings().removeClass('current-cat');
});

$('#compare a').click(function(e) {
    changePage('subpages/compare.html');
    getFollowersToCompare();
    tab = $('#compare');
    tab.addClass('current-cat');
    tab.siblings().removeClass('current-cat');
});

$('#brag a').click(function(e) {
    changePage('subpages/brag2.html');
    document.getElementById('lsidebar-wrapper').innerHTML="";
    tab = $('#brag');
    tab.addClass('current-cat');
    tab.siblings().removeClass('current-cat');

//Start Diiv Code
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
	//End Diiv code

});

/*
  $(document).ready(function() {
  alert("hi");
  // moved the function below from here to init() in main.js for local testing
  //	f();
  $(function() {
  
  $("form").submit(function() {

  $form = $(this);
  $.fancybox({
  'href': $form.attr("action") + "?" + $form.serialize(),
  'transitionIn' : 'elastic',
  'transitionOut' : 'elastic',
  'autoScale' : false,
  'width': '40%',
  'height': '100%',
  'type': 'iframe',
  
  });

  return false;

  });

  });

  });
*/
/*
//implementation for lightbox_me
$('.badgeLink').click(function(e) {
$('.popup').lightbox_me({
centered: true, 
});
e.preventDefault();
});  
*/
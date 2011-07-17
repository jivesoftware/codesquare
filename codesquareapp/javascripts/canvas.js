
$(document).ready(function() {
	alert("hi");
	// moved the function below from here to init() in main.js for local testing
	//	makeBadgeTable();
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

/*
//implementation for lightbox_me
$('.badgeLink').click(function(e) {
	$('.popup').lightbox_me({
		centered: true, 
		});
	e.preventDefault();
});  
*/
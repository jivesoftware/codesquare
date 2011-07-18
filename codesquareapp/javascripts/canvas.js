$(document).ready(function() {
makeBadgeTable();

	// moved the function below from init() in main.js to here for local testing
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
  
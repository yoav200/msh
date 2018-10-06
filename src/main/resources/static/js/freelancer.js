(function($) {
	"use strict"; // Start of use strict

	console.log("main script Ready");

	// jQuery for page scrolling feature - requires jQuery Easing plugin
	$('.page-scroll a').bind('click', function(event) {
		var $anchor = $(this);
		$('html, body').stop().animate({scrollTop : ($($anchor.attr('href')).offset().top - 50)
		}, 1250, 'easeInOutExpo');
		event.preventDefault();
	});

	// Highlight the top nav as scrolling occurs
	$('body').scrollspy({
		target : '.navbar-fixed-top',
		offset : 51
	});

	var $sidebar   = $("#actions-panel"), 
    $window    = $(window),
    offset     = $sidebar.offset(),
    topPadding = 15;
	
	// show hide scroll to top arrow
	$(window).scroll(function() {
		
		if ($(this).scrollTop() > 100) {
			$('#scroll-to-top').removeClass("hidden").fadeIn();
		} else {
			$('#scroll-to-top').fadeOut();
		}
		
		// for panel
		if ($window.scrollTop() > offset.top) {
            $sidebar.stop().animate({top : ($window.scrollTop() - offset.top + topPadding)});
        } else {
            $sidebar.stop().animate({top : 0});
        }
	});


	$.get("/messages/", function(data, status) {
		console.log("Data: " + data + "\nStatus: " + status);

		var title = 'Hi, <span>' + data.account.displayName + '</span>'
				+ '<img  src="' + data.account.profileImageUrl
				+ '" class="img-circle" style="max-height: 40px;" /> '
				+ 'You have a new message!';

		$("#messagesModal .modal-title").html(title);

		var messageBody = $("#messagesModal .modal-body");

		messageBody.empty();

		$.each(data.messages, function(i, order) {
			var cssClass = 'alert '
					+ ((data.messages[i].type === 'ERROR') ? 'alert-warning'
							: 'alert-info');
			var msg = "<div class='" + cssClass + "' role='alert'>"
					+ data.messages[i].message + "</div>";
			messageBody.append(msg);
		});

		$("#messagesModal").modal("show");
	});

	
	
	
	

})(jQuery); // End of use strict

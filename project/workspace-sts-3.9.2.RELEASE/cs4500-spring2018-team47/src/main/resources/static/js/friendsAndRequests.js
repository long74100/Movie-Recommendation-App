$(function() {

    $('#friend-list-link').click(function(e) {
		$("#friend-list").delay(100).fadeIn(100);
 		$("#request-list").fadeOut(100);
		$('#request-list-link').removeClass('active');
		$(this).addClass('active');
		e.preventDefault();
	});
	$('#request-list-link').click(function(e) {
		$("#request-list").delay(100).fadeIn(100);
 		$("#friend-list").fadeOut(100);
		$('#friend-list-link').removeClass('active');
		$(this).addClass('active');
		e.preventDefault();
	});

});
$(document).ready(function() {
	$('.movie-banner').slick({
		arrows : true,
		dots : true,
		slidesToShow : 8,
		slidesToScroll : 1,
		autoplay : true,
		autoplaySpeed : 2000,
		responsive : [ {
			breakpoint : 2500,
			settings : {
				slidesToShow : 5
			}
		} ]
	});
	$('.movies-in-theaters-info').slick({
		slidesToShow : 1,
		slidesToScroll : 1,
		arrows : false,
		fade : true,
		asNavFor : '.movies-in-theaters-nav'
	});
	$('.movies-in-theaters-nav').slick({
		slidesToShow : 3,
		slidesToScroll : 1,
		asNavFor : '.movies-in-theaters-info',
		centerMode : true,
		focusOnSelect : true
	});
	$('.movies-coming-soon-info').slick({
		slidesToShow : 1,
		slidesToScroll : 1,
		arrows : false,
		fade : true,
		asNavFor : '.movies-coming-soon-nav'
	});
	$('.movies-coming-soon-nav').slick({
		slidesToShow : 3,
		slidesToScroll : 1,
		asNavFor : '.movies-coming-soon-info',
		centerMode : true,
		focusOnSelect : true
	});
	$('.movies-popular-info').slick({
		slidesToShow : 1,
		slidesToScroll : 1,
		arrows : false,
		fade : true,
		asNavFor : '.movies-popular-nav'
	});
	$('.movies-popular-nav').slick({
		slidesToShow : 3,
		slidesToScroll : 1,
		asNavFor : '.movies-popular-info',
		centerMode : true,
		focusOnSelect : true
	});
	$('#body').show();
});
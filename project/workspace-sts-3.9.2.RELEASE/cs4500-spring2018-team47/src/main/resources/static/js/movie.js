function init() {
	const movieName = document.querySelector(".movie-title").innerText;
	const ratingButtons = document.querySelectorAll(".rating input");
	const submitReviewButton = document.querySelector("#writeReview");
	const movieId = document.querySelector("#imdbID").innerText;
	
	const currRating = document.querySelector("#currentRating").innerText;
	
	submitReviewButton.onclick = function() {
		var reviewBox = document.querySelector("#review-box").value;
		var encodedReview = encodeURI(reviewBox);
		var movie = document.querySelector("#imdbID").innerText;
		var encodedMovie = encodeURI(movie);
		var url = "/writeReview";
		var param = "review=" + encodedReview + "&movieId=" + encodedMovie;
		var xhr = new XMLHttpRequest();
		xhr.open("POST", url, true);
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xhr.send(param);
	}

	for (button in ratingButtons) {
		if (ratingButtons[button].value == currRating) {
			ratingButtons[button].checked = true;
		}
		
		ratingButtons[button].onclick = function() {	
			const value = this.value;
			uncheckButtons(value);
			const url = "/movie/rating";
			var encodedRating = encodeURI(value);
			var encodedMovieId = encodeURI(movieId);
			var param = "rating=" + encodedRating + "&movieId=" + encodedMovieId;
			var xhr = new XMLHttpRequest();
			xhr.open("POST", url, true);
			xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			xhr.send(param);
			
		}
	}
		
	function uncheckButtons(val) {
		for (button in ratingButtons) {
			if (ratingButtons[button].value != val) {
				ratingButtons[button].checked = false;
			}
		}
	}
}

document.addEventListener("DOMContentLoaded", init);
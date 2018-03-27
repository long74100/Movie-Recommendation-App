function init() {
	const movieName = document.querySelector(".movie-title").innerText;
	const ratingButtons = document.querySelectorAll(".rating input");
	const submitReviewButton = document.querySelector("#writeReview");
	
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
		ratingButtons[button].onclick = function() {
			const value = this.value;
			const url = "/movie/rating";

			// post rating
			let payload = JSON.stringify({movie: movieName, rating: value});
						
			fetch(url, {
				  method: 'post',
				  headers: {
					'Accept': 'application/json, text/plain, */*',
				    'Content-Type': 'ap	plication/json'
				  },
				  body: payload
			});
			
		}
	}
}

document.addEventListener("DOMContentLoaded", init);
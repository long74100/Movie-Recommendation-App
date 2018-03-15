function init() {
	const movieName = document.querySelector(".movie-title").innerText;
	const ratingButtons = document.querySelectorAll(".rating input");

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
				    'Content-Type': 'application/json'
				  },
				  body: payload
			});
			
		}
	}
}

document.addEventListener("DOMContentLoaded", init);
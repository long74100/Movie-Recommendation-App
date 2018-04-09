function init() {
	const rating = document.querySelector(".rating");
	const reviewBox = document.querySelector("#review-box");
	const userId = document.querySelector("#userID").innerText;
	const reviews = document.querySelector("#reviews");
	const removeReviewBtns = document.querySelectorAll(".remove-review");
	
	if (userId == "") {
		rating.classList.add("d-none");
		reviewBox.classList.add("d-none");
		submitReviewButton.classList.add("d-none");
		return;
	}
	
	const movieName = document.querySelector(".movie-title").innerText;
	const ratingButtons = document.querySelectorAll(".rating input");
	const submitReviewButton = document.querySelector("#writeReview");
	
	const movieId = document.querySelector("#imdbID").innerText;
	const currRating = document.querySelector("#currentRating").innerText;
	
	submitReviewButton.onclick = function() {
		var encodedReview = encodeURI(reviewBox.value);
		var movie = document.querySelector("#imdbID").innerText;
		var encodedMovie = encodeURI(movie);
		var url = "/writeReview";
		var param = "review=" + encodedReview + "&movieId=" + encodedMovie;
		var xhr = new XMLHttpRequest();
		xhr.open("POST", url, true);
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xhr.send(param);
	}
	
	var addtolistButton = document.querySelector("#addToList");
		// to get the list name
		addtolistButton.onclick = function() {
		var listObject = document.getElementById("listOpt");
		var selectedList = listObject.options[listObject.selectedIndex].value;
		var movieId = document.querySelector("#imdbID").innerText;
		const movieName = document.querySelector(".movie-title").innerText;
		var encodedMovie = encodeURI(movieId);
		var encodedMovieName = encodeURI(movieName);
		var encodedList = encodeURI(selectedList);
		const url = "/addMovieToList";
		var param = "movie=" + encodedMovieName + "&movieId=" + encodedMovie + "&movieList=" + encodedList;
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
	
	for (let btn of removeReviewBtns) {
		btn.addEventListener("click", removeReview);
		btn.addEventListener("click", removeReviewFromPage);
	}
	
	function removeReview(ev) {
		let p = ev.target.parentElement;
		reviewer_id = p.firstElementChild.innerText;
		review_desc = p.parentElement.children[1].firstElementChild.innerText;
		
		let url = "/movie/removeReview";
		let encodedUserId = encodeURI(reviewer_id);
		let encodedMovieId = encodeURI(movieId);
		let encodedReviewDesc = encodeURI(review_desc);
		let param = "userId=" + encodedUserId + "&movieId=" + encodedMovieId + "&desc=" + review_desc;
		let xhr = new XMLHttpRequest();
		xhr.open("POST", url, true);
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xhr.send(param);

	}
	
	function removeReviewFromPage(ev) {
		const evParent = ev.target.parentElement;
		const reviewDiv = evParent.parentElement.parentElement;
		reviews.removeChild(reviewDiv);
	}
	
}



document.addEventListener("DOMContentLoaded", init);
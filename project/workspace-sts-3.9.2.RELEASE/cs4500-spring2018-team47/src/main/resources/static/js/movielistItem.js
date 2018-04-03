function init() {
	var movieDropdown = document.getElementById("movielist-dropdown-menu");
	
	window.onclick = hideMenu;
	
	function hideMenu() {
		movieDropdown.style.display = 'none';
	}
}

// this is to show the dropdown operation list menu: prod to friend, delete or add to another list
function showOpMenu(event) {
	var movieDropdown = document.getElementById("movielist-dropdown-menu");
	var clickPosition = getMenuPosition(event);
	var movieItem = document.querySelector("movieItems");
	if(event.which == 3) {
		movieDropdown.style.marginLeft = clickPosition.x + 'px';
		movieDropdown.style.marginTop = clickPosition.y + 'px';
		movieDropdown.style.display = 'block';
	}
	return false;
}


function getMenuPosition(event) {
	var positionX = event.pageX;
	var positionY = event.pageY;
	return {x: positionX, y:positionY}
}

// this is to delete the movie from the current movie list
function deleteMovie(movieId, thisButton) {
	var movieListName = document.getElementById("currentMovieList").innerText;
	var encodedListName = encodeURI(movieListName);
	var encodedMovieId = encodeURI(movieId);
	var url = "/deleteMovie";
	var param = "listName=" + encodedListName + "&movieId=" + encodedMovieId;
	var xhr = new XMLHttpRequest();
	xhr.open("POST", url, true);
	xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	xhr.send(param);
	setTimeout(function() {
		location.reload(true);
		}, 
		150);
}

document.addEventListener("DOMContentLoaded", init);
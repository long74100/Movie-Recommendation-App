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

function showFriendsToProd(movieName, thisButton) {
	var friendToProd = document.getElementById("friendList-for-prod");
	var clickPosition = getMenuPosition(event);
	document.getElementById("prod-movie-name").innerHTML = movieName;
	friendToProd.style.display = "block";
}


function addRecipient(userName, userId) {
	var userContainer = document.getElementById("prod-to-friend-input-container");
	var elem = document.getElementById("prodUserId" + userId);
	if(elem != null) {
		alert("You already added this recipient.");
	}
	else {
		var innerHtml = 
			userContainer.innerHTML + 
			"<div style=\"width:auto;" + " border: solid; border-radius:8px; border-width:thin; background:black; color:wheat; float: left;\"" +
					" id=\"" + "prodUserId" + userId + "\"\">" +
				"<span>" +
					"<p class=\"RecipientUserName\" id=\"RecipientUserName\" style=\"float:left; margin-bottom:0px;\">" 
					+ userName + 
					"</p>" + 
					"<p class=\"prodRecipientId\" id=\"prodRecipientId\" style=\"display: none;\">" + userId + "</p>" +
					"<button class=\"deleteUserBtn\" type=\"submit\" " +
					"id=\"deleteUserBtn\" onclick=\"deleteRecipient(\'" + userId + "\', this)\"" +
					"style=\"width:20px; height:100%; float: left; padding-left: 2px; padding-right: 2px; text-align:left; background:transparent; border:none; color:white;\">" +
					"&times;</button>" +
				"</span>" +
			"</div>";
		document.getElementById("prod-to-friend-input-container").innerHTML = innerHtml;
	}
	
}

function cleanInput() {
	document.getElementById("prod-to-friend-input-container").innerHTML = "";
}

function deleteRecipient(userId, thisButton) {
	var elem = document.getElementById("prodUserId" + userId);
	elem.parentNode.removeChild(elem);
	return false;
}


function closeProdMenu() {
	var prodFriendList = document.getElementById("friendList-for-prod");
	document.getElementById("prod-to-friend-input-container").innerHTML = "";
	prodFriendList.style.display = 'none';
}

document.addEventListener("DOMContentLoaded", init);
function init() {
	var movieDropdown = document.getElementById("movielist-dropdown-menu");
	var searchContent = document.getElementById("usersearchbar").value;
	if(searchContent == "") {
		$("div.friend-item-block").css("display", "block");
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

function showFriendsToProd(movieName, movieId, movieDBID, moviePoster, thisButton) {
	var friendToProd = document.getElementById("friendList-for-prod");
	var clickPosition = getMenuPosition(event);
	document.getElementById("prod-movie-name").innerHTML = movieName;
	document.getElementById("prod-movie-id").innerHTML = movieId;
	document.getElementById("prod-movie-dbID").innerHTML = movieDBID;
	document.getElementById("movie-poster-prod").innerHTML = moviePoster;
	friendToProd.style.display = "block";
	return false;
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
			"<div style=\"width:auto;" + " border: solid; border-radius:4px; border-width:thin; background:black; color:wheat; float: left;\"" +
					" id=\"" + "prodUserId" + userId + "\" class=\"userBlock\">" +
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

// this functions will send out the movie suggestions to friends
function sendOutProd() {
	// also needs movie title and movie id
	var allUserName = document.getElementsByClassName("RecipientUserName");
	var allUserId = document.getElementsByClassName("prodRecipientId");
	var movieImdbId = document.getElementById("prod-movie-id").innerHTML;
	var movieTitle = document.getElementById("prod-movie-name").innerHTML;
	var movieDBId = document.getElementById("prod-movie-dbID").innerHTML;
	var moviePoster = document.getElementById("movie-poster-prod").innerHTML;
	var comment = document.querySelector("#comment-area").value;
	var AllUserNameContainer = [];
	var AllIdContainer = [];
	for(var i = 0; i < allUserName.length; i++) {
		AllUserNameContainer.push(allUserName[i].innerHTML);
		AllIdContainer.push(allUserId[i].innerHTML);
	}
	var allUserNameInString = AllUserNameContainer.join();
	var allIdsInString = AllIdContainer.join();
	
	// start encoding information and send out
	var encodedMovieName = encodeURI(movieTitle);
	var encodedMovieImdbID = encodeURI(movieImdbId);
	var encodedMovieDBId = encodeURI(movieDBId);
	var encodedAllUserName = encodeURI(allUserNameInString);
	var encodedAllUserId = encodeURI(allIdsInString);
	var encodedComment = encodeURI(comment);
	
	var url = "/prodToFriends";
	var param = "movieName=" + encodedMovieName + "&movieId=" + encodedMovieImdbID + 
				"&movieDBId=" + encodedMovieDBId + "&allUserName=" + encodedAllUserName +
				"&allUserId=" + encodedAllUserId + "&comment=" + encodedComment + "&poster=" + moviePoster;
	var xhr = new XMLHttpRequest();
	xhr.open("POST", url, true);
	xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	xhr.send(param);
	setTimeout(function() {
		location.reload(true);
		}, 
		150);
}


function cleanInput() {
	document.getElementById("prod-to-friend-input-container").innerHTML = "";
	document.querySelector("#comment-area").value = "";
}

function deleteRecipient(userId, thisButton) {
	var elem = document.getElementById("prodUserId" + userId);
	elem.parentNode.removeChild(elem);
	return false;
}


function closeProdMenu() {
	var prodFriendList = document.getElementById("friendList-for-prod");
	document.getElementById("prod-to-friend-input-container").innerHTML = "";
	document.querySelector("#comment-area").value = "";
	prodFriendList.style.display = 'none';
}


function searchUser() {
	var searchContent = document.getElementById("usersearchbar").value;
	var hasFriend = false;
	if(searchContent != "") {
		// search username
		var singleUserBlocks = document.getElementsByClassName("friend-item-block");
		var allUsers = $("div.friend-item-block").find('p');
		for(var i = 0; i < allUsers.length; i ++) {
			if(allUsers.get(i).innerHTML == searchContent) {
				hasFriend = true;
				singleUserBlocks[i].style["display"] = "block";
				document.getElementById("usersearchbar").value = "";
			}
			else {
				singleUserBlocks[i].style["display"] = "none";
			}
		}
		if(!hasFriend) {
			alert("Friend not found!");
		}
	}
	else {
		$("div.friend-item-block").css("display", "block");
	}
}

document.addEventListener("DOMContentLoaded", init);
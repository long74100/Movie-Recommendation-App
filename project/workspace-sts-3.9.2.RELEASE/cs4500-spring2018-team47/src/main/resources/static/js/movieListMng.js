function init() {
	
	var movielistop = document.getElementById("movielist-operator");
	
	window.onclick = closeMenu;
	
	function closeMenu() {
		movielistop.style.display = 'none';
	}
	
	
	const addListButton = document.querySelector("#movieCreateBtn");
	
	// this is to create a new movie list
	addListButton.onclick = function() {
		var newNameInput = document.getElementById("newListName").value;
		if(newNameInput.trim() != "") {
			var encodedListName = encodeURI(newNameInput);
			var url = "/createMovieList";
			var param = "listName=" + encodedListName;
			var xhr = new XMLHttpRequest();
			xhr.open("POST", url, true);
			xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			xhr.send(param);
			setTimeout(function() {
				location.reload(true);
				}, 
				150);
		}
		else {
			alert("Can't create list without name or only space name");
		}
	}
}

function showMenu(event) {
	var menu = document.getElementById("movielist-operator");
	var clickPosition = returnPosition(event);
	if(event.which == 3) {
		menu.style.marginLeft = clickPosition.x + 'px';
		menu.style.marginTop = clickPosition.y + 'px';
		menu.style.display = 'block';
	}
	return false;
	
}

function returnPosition(event) {
	var positionX = event.pageX;
	var positionY = event.pageY;
	return {x: positionX, y:positionY}
}

/**
 * This is to delete the movie list
 * @param listName name of movie list
 * @param thisButton delete button
 * @returns the movie list with given one deleted
 */
function deleteList(listName, thisButton) {
	var encodedListName = encodeURI(listName);
	var url = "/deleteList";
	var param = "listName=" + encodedListName;
	var xhr = new XMLHttpRequest();
	xhr.open("POST", url, true);
	xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	xhr.send(param);
	setTimeout(function() {
		location.reload(true);
		}, 
		150);
}

/**
 * This is to clean the movie list, that is to make the movie list empty
 * @param listName the list that will be clean up
 * @param thisButton the clean button
 * @returns an empty movie list with the given name 
 */
function cleanList(listName, thisButton) {
	var encodedListName = encodeURI(listName);
	var url = "/cleanList";
	var param = "listName=" + encodedListName;
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
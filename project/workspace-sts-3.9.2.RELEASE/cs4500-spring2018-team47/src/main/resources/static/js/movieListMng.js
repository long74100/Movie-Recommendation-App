function init() {
	
	const addListButton = document.querySelector("#movieCreateBtn");
	
	// this is to create a new movie list
	addListButton.onclick = function() {
		var newNameInput = document.getElementById("newListName").value;
		if(newNameInput != "") {
			var encodedListName = encodeURI(newNameInput);
			var url = "/createMovieList";
			var param = "listName=" + encodedListName;
			var xhr = new XMLHttpRequest();
			xhr.open("POST", url, true);
			xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			let response = xhr.send(param);
			console.log(response);
		}
		else {
			alert("Can't create list without name or only space name");
		}
	}
}



document.addEventListener("DOMContentLoaded", init);
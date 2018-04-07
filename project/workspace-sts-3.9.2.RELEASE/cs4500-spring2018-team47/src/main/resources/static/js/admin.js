function init() {
	
	let banButton = document.querySelector("#ban-button");

	if (banButton) {
		let buttonText = banButton.innerText;
		banButton.classList = buttonText == "Ban User" ? "btn btn-danger" : "btn btn-success";
		banButton.addEventListener("click", banOrUnbanUser);
	}
	
	function banOrUnbanUser(ev) {
		ev.preventDefault();
		updateButton(banButton.innerText);

	}
	
	function updateButton(text) {
		banButton.innerText = text == "Ban User" ? "Unban User" : "Ban User";
		banButton.classList = text == "Ban User" ? "btn btn-success" : "btn btn-danger";


	}
	

}







document.addEventListener("DOMContentLoaded", init);
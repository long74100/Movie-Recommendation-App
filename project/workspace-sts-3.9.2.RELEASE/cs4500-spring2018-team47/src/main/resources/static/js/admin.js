function init() {
	
	let banButton = document.querySelector("#ban-button");
	let userId = document.querySelector("#profileUserId").innerText;
	
	if (banButton) {
		let buttonText = banButton.innerText;
		banButton.classList = buttonText == "Ban User" ? "btn btn-danger" : "btn btn-success";
		banButton.addEventListener("click", banOrUnbanUser);
	}
	
	function banOrUnbanUser(ev) {
		ev.preventDefault();
		let url = "/admin/ban";
		let encodedUserId = encodeURI(userId);
		let encodedUserStatus = encodeURI(banButton.innerText == "Ban User" ? 1 : 0);
		let param = "userId=" + encodedUserId + "&userStatus=" + encodedUserStatus;
		let xhr = new XMLHttpRequest();
		xhr.open("POST", url, true);
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xhr.send(param);
	
		updateButton(banButton.innerText);
		
	}
	
	function updateButton(text) {
		banButton.innerText = text == "Ban User" ? "Unban User" : "Ban User";
		banButton.classList = text == "Ban User" ? "btn btn-success" : "btn btn-danger";


	}
	

}







document.addEventListener("DOMContentLoaded", init);
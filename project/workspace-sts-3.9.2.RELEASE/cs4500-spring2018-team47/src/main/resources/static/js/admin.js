function init() {
	let banButton = document.querySelector("#ban-button");

	if (banButton) {
		let buttonText = banButton.innerText;
		banButton.classList = buttonText == "Ban User" ? "btn btn-danger" : "btn btn-success";
		banButton.addEventListener("click", banOrUnbanUser);
	} else {
		let banButtons = document.querySelectorAll(".unban-btn");
		for (let btn of banButtons) {
			let userId = btn.parentElement.firstElementChild.innerText;
			btn.addEventListener("click", unbanUser);
			btn.addEventListener("click", removeFromPage);
		}
	}
	
	function removeFromPage(ev) {
		let banned = document.querySelector("#banned");
		banned.removeChild(ev.target.parentElement);
	}
	
	function unbanUser(ev) {
		let userId = ev.target.parentElement.firstElementChild.innerText;
		updateUserActiveStatus(userId, 0);
		
	}
	
	function banOrUnbanUser(ev) {
		ev.preventDefault();
		let profileUserId = document.querySelector("#profileUserId");
		let userId = profileUserId ? profileUserId.innerText : "";
		updateUserActiveStatus(userId, banButton.innerText == "Ban User" ? 1 : 0);
		updateButton(banButton.innerText);
		
	}
	
	function updateUserActiveStatus(id, statusTo) {
		let url = "/admin/ban";
		let encodedUserId = encodeURI(id);
		let encodedUserStatus = encodeURI(statusTo);
		let param = "userId=" + encodedUserId + "&userStatus=" + encodedUserStatus;
		let xhr = new XMLHttpRequest();
		xhr.open("POST", url, true);
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xhr.send(param);

	}
	
	function updateButton(text) {
		banButton.innerText = text == "Ban User" ? "Unban User" : "Ban User";
		banButton.classList = text == "Ban User" ? "btn btn-success" : "btn btn-danger";

	}
	

}







document.addEventListener("DOMContentLoaded", init);
$(function() {

	$('#friend-list-link').click(function(e) {
		$("#friend-list").delay(100).fadeIn(100);
		$("#request-list").fadeOut(100);
		$('#request-list-link').removeClass('active');
		$(this).addClass('active');
		e.preventDefault();

		location.reload();
	});
	$('#request-list-link').click(function(e) {
		$("#request-list").delay(100).fadeIn(100);
		$("#friend-list").fadeOut(100);
		$('#friend-list-link').removeClass('active');
		$(this).addClass('active');
		e.preventDefault();
	});
});

function addFriend(value, thisButton) {
	var encodedAcceptRequest = encodeURI(value);
	var url = "/acceptRequest";
	var param = "senderID=" + encodedAcceptRequest;
	var xhr = new XMLHttpRequest();
	xhr.open("POST", url, true);
	xhr.setRequestHeader("Content-type",
			"application/x-www-form-urlencoded");
	xhr.send(param);
	thisButton.innerHTML = "Accepted!";
}

function init() {
	let deleteFriendBtns = document.querySelectorAll(".delete-friend");
	for (let btn of deleteFriendBtns) {
		btn.addEventListener("click", deleteFriend);
	}
	
	function deleteFriend(ev) {
		ev.preventDefault();
		let friends = document.querySelector("#friend-list").firstElementChild;
		let topElement = ev.target.parentElement.parentElement;
		let friendID = topElement.firstElementChild.innerText;
		let encodedFriendID = encodeURI(friendID);
		let url = "/deleteFriend";
		let param = "friendID=" + encodedFriendID;
		var xhr = new XMLHttpRequest();
		xhr.open("POST", url, true);
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xhr.send(param);
		friends.removeChild(topElement.parentElement.parentElement);
	}
}

document.addEventListener("DOMContentLoaded", init);
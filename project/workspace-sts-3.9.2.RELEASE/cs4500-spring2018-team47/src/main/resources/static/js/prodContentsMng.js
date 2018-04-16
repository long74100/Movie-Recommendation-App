function init() {
	
	
}

function deleteProd(senderId, receiverId, movieId, yourId, thisButton) {
	var role = "";
	if(senderId == yourId) {
		role = "sender";
	}
	if(receiverId == yourId) {
		role = "receiver";
	}
	
	var encodedSenderId = encodeURI(senderId);
	var encodedReceiverId = encodeURI(receiverId);
	var encodedMovieId = encodeURI(movieId);
	var encodedTarget = encodeURI(role);
	var param = "senderId=" + encodedSenderId + "&receiverId=" + encodedReceiverId + "&prodMovie=" + encodedMovieId + "&side=" + encodedTarget;
	var url = "/deleteProd"
	var xhr = new XMLHttpRequest();
	xhr.open("POST", url, true);
	xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	xhr.send(param);
	setTimeout(function() {
		location.reload(true);
		}, 
		1000);
}


function recallProd(senderId, receiverId, movieId, prodTime, thisButton) {
	var prodDate = Date.parse(prodTime);
	var recallTryDate = Date.now();
	var difference = Math.abs(recallTryDate - prodDate);
	/** Math.ceil(timeDiff / (1000 * 3600 * 24));*/
	var differenceInMins = Math.ceil(difference / (1000 * 60));
	if(differenceInMins > 10) {
		//alert("Sorry, you can't recall after 10 minutes.");
	}
	//else {
		var encodedSenderId = encodeURI(senderId);
		var encodedReceiverId = encodeURI(receiverId);
		var encodedMovieId = encodeURI(movieId);
		var param = "senderId=" + encodedSenderId + "&receiverId=" + encodedReceiverId + "&prodMovie=" + encodedMovieId;
		var url = "/recallProd"
		var xhr = new XMLHttpRequest();
		xhr.open("POST", url, true);
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xhr.send(param);
		setTimeout(function() {
			location.reload(true);
			}, 
			1000);
	//}
}


document.addEventListener("DOMContentLoaded", init);
function init() {
	
	
}

function findAFriend() {
	var searchFriend = document.getElementById("friend-search-bar").value;
	//alert(seachFriend);
	var hasFriend = false;
	if(searchFriend != "") {
		var singleFriendBlock = document.getElementsByClassName("friend-single-for-view-prod-item");
		var allFriends = $("div.friend-single-for-view-prod-item").find('p');
		for(var i = 0; i < allFriends.length; i++) {
			if(allFriends.get(i).innerHTML == searchFriend) {
				hasFriend = true;
				singleFriendBlock[i].style["display"] = "block";
				document.getElementById("friend-search-bar").value = "";
			}
			else {
				singleFriendBlock[i].style["display"] = "none";
			}
		}
		if(!hasFriend) {
			alert("Friend not found!");
		}
	}
	else {
		$("div.friend-single-for-view-prod-item").css("display", "block");
	}
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
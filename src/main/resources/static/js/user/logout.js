function logout() {
	localStorage.removeItem("user.id");
	localStorage.removeItem("user.lastLogin"); // 넣을? 말?
	location.href = "/";
}

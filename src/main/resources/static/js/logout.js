function logout() {
	localStorage.removeItem("user.id");
	location.href = "/";
}

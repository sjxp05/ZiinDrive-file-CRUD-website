function showMenu() {
	document.querySelector(".overlay").classList.add("active");
	document.querySelector(".sidemenu").classList.add("active");

	if (location.href === "http://localhost:8080/favorites") {
		document.getElementById("favoritesMenu").classList.add("active");
	} else if (location.href === "http://localhost:8080/bin") {
		document.getElementById("trashbinMenu").classList.add("active");
	} else {
		document.getElementById("allFilesMenu").classList.add("active");
	}
}

function closeMenu() {
	document.querySelector(".overlay").classList.remove("active");
	document.querySelector(".sidemenu").classList.remove("active");
}

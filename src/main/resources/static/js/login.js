function login() {
	const idInput = document.getElementById("idInput");
	const pwInput = document.getElementById("pwInput");

	const id = idInput.value.trim();
	const pw = pwInput.value.trim();

	console.log(id + " / " + pw);

	if (id === null || id.length === 0) {
		idInput.value = "";
		pwInput.value = "";
		return;
	}

	localStorage.setItem("user.id", id);
	location.href = "/files";
}

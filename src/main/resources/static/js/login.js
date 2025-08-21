function login() {
	const idInput = document.getElementById("idInput");
	const pwInput = document.getElementById("pwInput");

	const id = idInput.textContent;
	const pw = pwInput.textContent;

	location.href = "/" + id + "/files";
}

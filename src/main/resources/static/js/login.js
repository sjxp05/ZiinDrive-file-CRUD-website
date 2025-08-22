document.addEventListener("DOMContentLoaded", () => {
	sessionStorage.setItem("idValidated", false);
});

function login() {
	const idInput = document.getElementById("idInput");
	const pwInput = document.getElementById("pwInput");

	const id = idInput.value.trim();
	const pw = pwInput.value;

	if (id === null || id.length === 0 || pw === null || pw.length === 0) {
		alert("ID 또는 비밀번호가 잘못되었습니다.");
		return;
	}

	fetch("/api/login", {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			id: id,
			password: pw,
		}),
	})
		.then((res) => {
			if (!res.ok) {
				throw res;
			}
		})
		.then(() => {
			console.log("로그인 성공");
			localStorage.setItem("user.id", id);
			location.href = "/files";
		})
		.catch(async (err) => {
			msg = await err.text();
			console.error("로그인 중 오류:", msg);
			alert("ID 또는 비밀번호가 잘못되었습니다.");

			if (msg === "uncorrect password") {
				document.getElementById("resetPw").style.visibility = "visible";
			}
		});
}

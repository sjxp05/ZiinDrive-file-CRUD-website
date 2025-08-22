function login() {
	const idInput = document.getElementById("idInput");
	const pwInput = document.getElementById("pwInput");

	const id = idInput.value.trim();
	const pw = pwInput.value.trim();

	if (id === null || id.length === 0 || pw === null || pw.length === 0) {
		alert("ID와 비밀번호를 올바르게 입력해 주세요!");

		idInput.value = "";
		pwInput.value = "";
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
			alert(msg);
		});
}

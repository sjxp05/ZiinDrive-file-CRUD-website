function signup() {
	const loginId = document.getElementById("idInput").value.trim() || "";
	const password = document.getElementById("pwInput").value;
	const nickname = document.getElementById("nickInput").value.trim() || "";
	const email = document.getElementById("emailInput").value.trim() || "";

	if (password !== document.getElementById("pwConfirm").value) {
		alert("비밀번호를 다시 확인해 주세요.");
		return;
	}

	fetch("/api/users/signup", {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			loginId: loginId,
			password: password,
			nickname: nickname,
			email: email,
		}),
	})
		.then((res) => {
			if (!res.ok) {
				throw res;
			}
		})
		.then(() => {
			console.log("회원가입 성공");
			alert("회원가입에 성공하였습니다.");
			location.href = "/";
		})
		.catch(async (err) => {
			const msg = await err.text();
			alert(msg);

			console.error("회원가입 실패:", err.status);
		});
}

function checkDuplicateId() {
	const loginId = document.getElementById("idInput").value.trim() || "";

	fetch("/api/users/id", {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			loginId: loginId,
		}),
	})
		.then(async (res) => {
			if (!res.ok) {
				throw res;
			}
		})
		.then(() => {
			console.log("ID 중복 체크 결과: 200 OK");
			alert("사용할 수 있는 ID입니다.");
		})
		.catch(async (err) => {
			const msg = await err.text();
			console.error("ID 중복 체크 결과:", err.status);
			alert(msg);
		});
}

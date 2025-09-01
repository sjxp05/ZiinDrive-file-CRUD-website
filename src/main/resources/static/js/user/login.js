document.addEventListener("DOMContentLoaded", () => {
	// sessionStorage.setItem("idValidated", false); 엥 이건 회원가입에 넣어야 되는뎅

	const lastLogin = localStorage.getItem("user.lastLogin");
	const now = new Date();
	const threeMonthsAgo = now.setMonth(now.getMonth() - 3);

	if (lastLogin === null || lastLogin < threeMonthsAgo) {
		localStorage.removeItem("user.id");
	}
});

function login() {
	const idInput = document.getElementById("idInput");
	const pwInput = document.getElementById("pwInput");

	const loginId = idInput.value.trim();
	const password = pwInput.value;

	if (
		loginId === null ||
		loginId.length === 0 ||
		password === null ||
		password === 0
	) {
		alert("ID 또는 비밀번호가 잘못되었습니다.");
		return;
	}

	fetch("/api/users/login", {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			loginId: loginId,
			password: password,
		}),
	})
		.then((res) => {
			if (!res.ok) {
				if (res.status === 401) {
					document.getElementById("resetPw").style.visibility =
						"visible";
				}
				throw res;
			}
			return res;
		})
		.then(async (res) => {
			console.log("로그인 성공");

			const id = await res.text();
			localStorage.setItem("user.id", id);
			localStorage.setItem("user.lastLogin", new Date());
			location.href = "/files";
		})
		.catch(async (err) => {
			msg = await err.text();
			console.error("로그인 중 오류:", msg);
			alert("ID 또는 비밀번호가 잘못되었습니다.");
		});
}

function logout() {
	localStorage.removeItem("user.id");
	localStorage.removeItem("user.lastLogin"); // 넣을? 말?
	location.href = "/";
}

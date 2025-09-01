document.addEventListener("DOMContentLoaded", () => {
	const div = document.querySelector(".topDiv");
	const key = div.id;

	if (
		localStorage.getItem("user.id") === null ||
		localStorage.getItem("user.lastLogin") === null
	) {
		console.error("there's no user id");
		alert("사용자 정보를 찾을 수 없습니다. 다시 시도해 주세요.");
		location.href = "/login";
	}

	fetch("/api/users/info", {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: {
			id: localStorage.getItem("user.id"),
			key: key,
		},
	})
		.then((res) => {
			if (!res.ok) {
				throw res;
			}
			return res;
		})
		.then(async (res) => {
			const currentInfo = await res.text();
			const input = document.getElementById(
				key === "nickname" ? "nickInput" : "emailInput"
			);

			input.value = currentInfo;
			input.focus();
		})
		.catch(async (err) => {
			console.error("불러오기 실패:", err.status);
		});
});

function showPassword() {
	const pwInput = document.getElementById("pwInput");
	const showPasswordBt = document.getElementById("showPasswordBt");

	if (showPasswordBt.classList.contains("active")) {
		pwInput.setAttribute("type", "password");
	} else {
		pwInput.setAttribute("type", "text");
	}

	showPasswordBt.classList.toggle("active");
}

function showPassword2() {
	const pwInput2 = document.getElementById("pwInput2");
	const showPasswordBt2 = document.getElementById("showPasswordBt2");

	if (showPasswordBt2.classList.contains("active")) {
		pwInput2.setAttribute("type", "password");
	} else {
		pwInput2.setAttribute("type", "text");
	}

	showPasswordBt2.classList.toggle("active");
}

function confirmPassword() {
	const pwInput = document.getElementById("pwInput");
	const pw = pwInput.value.trim();

	if (
		localStorage.getItem("user.id") === null ||
		localStorage.getItem("user.lastLogin") === null
	) {
		console.error("there's no user id");
		alert("사용자 정보를 찾을 수 없습니다. 다시 시도해 주세요.");
		location.href = "/login";
	}

	fetch("/api/users/account", {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			id: localStorage.getItem("user.id"),
			password: pw,
		}),
	})
		.then((res) => {
			if (!res.ok) {
				throw res;
			}
		})
		.then(() => {
			console.log("비밀번호 일치");
		})
		.catch(async (err) => {
			console.log("비밀번호 검사 실패", err.status);
			alert(await err.text());
		});
}

function changePassword() {
	const pwInput = document.getElementById("pwInput");
	const pwInput2 = document.getElementById("pwInput2");
	const pw = pwInput.value.trim();

	if (pw !== pwInput2.value.trim()) {
		alert("비밀번호 확인란과 일치하지 않습니다.");
		return;
	}

	if (
		localStorage.getItem("user.id") === null ||
		localStorage.getItem("user.lastLogin") === null
	) {
		console.error("there's no user id");
		alert("사용자 정보를 찾을 수 없습니다. 다시 시도해 주세요.");
		location.href = "/login";
	}

	fetch("/api/users", {
		method: "PATCH",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			id: localStorage.getItem("user.id"),
			password: pw,
		}),
	})
		.then((res) => {
			if (!res.ok) {
				throw res;
			}

			alert("수정 사항이 반영되었습니다.");
			history.back(); // 로그인 or 사용자 설정 비번수정화면
		})
		.catch(async (err) => {
			console.error("수정 실패:", err.status);
			alert(await err.text());
		});
}

function changeNickname() {
	const nickInput = document.getElementById("nickInput");
	const nickname = nickInput.value.trim();

	if (
		localStorage.getItem("user.id") === null ||
		localStorage.getItem("user.lastLogin") === null
	) {
		console.error("there's no user id");
		alert("사용자 정보를 찾을 수 없습니다. 다시 시도해 주세요.");
		location.href = "/login";
	}

	fetch("/api/users", {
		method: "PATCH",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			id: localStorage.getItem("user.id"),
			nickname: nickname,
		}),
	})
		.then((res) => {
			if (!res.ok) {
				throw res;
			}

			alert("수정 사항이 반영되었습니다.");
			location.href = "/user/info";
		})
		.catch(async (err) => {
			console.error("수정 실패:", err.status);
			alert(await err.text());
		});
}

function changeEmail() {
	const emailInput = document.getElementById("emailInput");
	const email = emailInput.value.trim();

	if (
		localStorage.getItem("user.id") === null ||
		localStorage.getItem("user.lastLogin") === null
	) {
		console.error("there's no user id");
		alert("사용자 정보를 찾을 수 없습니다. 다시 시도해 주세요.");
		location.href = "/login";
	}

	fetch("/api/users", {
		method: "PATCH",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			id: localStorage.getItem("user.id"),
			email: email,
		}),
	})
		.then((res) => {
			if (!res.ok) {
				throw res;
			}

			alert("수정 사항이 반영되었습니다.");
			location.href = "/user/info";
		})
		.catch(async (err) => {
			console.error("수정 실패:", err.status);
			alert(await err.text());
		});
}

function deleteAccount() {
	confirmPassword();

	const really = confirm(
		"정말 삭제를 진행하시겠습니까?\n삭제된 사용자의 정보는 복구할 수 없습니다."
	);

	if (!really) {
		return;
	}

	fetch("/api/users/account/" + localStorage.getItem("user.id"), {
		method: "DELETE",
	})
		.then((res) => {
			if (!res.ok) {
				throw new Error(res.status);
			}

			console.log("탈퇴 정상 처리 완료");
			alert("정상적으로 처리되었습니다.");
			location.href = "/";
		})
		.catch((err) => {
			console.error("계정 삭제 실패:", err);
			alert("삭제 도중 문제가 발생하였습니다. 다시 시도해 주세요.");
		});
}

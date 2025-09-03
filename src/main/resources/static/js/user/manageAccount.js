document.addEventListener("DOMContentLoaded", () => {
	const div = document.querySelector(".topDiv");
	const key = div.dataset.id;

	if (location.href.endsWith("/user/verify/id")) {
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

	if (key === "nickname" || key === "email") {
		fetch("/api/users/info", {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify({
				id: localStorage.getItem("user.id"),
				key: key,
			}),
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
				input.select();
			})
			.catch(async (err) => {
				console.error("불러오기 실패:", err.status);
			});
	}
});

function verifyById() {
	const loginId = document.getElementById("idInput").value.trim();
	const email = document.getElementById("emailInput").value.trim();

	fetch("/api/users/verify/id", {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			loginId: loginId,
			email: email,
		}),
	})
		.then((res) => {
			if (!res.ok) {
				throw new Error(res.status);
			}
			return res;
		})
		.then(async (res) => {
			const data = await res.json();
			localStorage.setItem("user.id", data.id); // 다음 페이지에서 사용될 아이디

			location.href = "/user/reset?key=password";
		})
		.catch((err) => {
			console.error("인증 실패:", err);
			alert(
				"ID 또는 이메일 주소가 일치하지 않습니다. 다시 시도해 주세요."
			);
		});
}

async function verifyByPassword() {
	const pw = document.getElementById("pwInput").value;

	if (
		localStorage.getItem("user.id") === null ||
		localStorage.getItem("user.lastLogin") === null
	) {
		console.error("there's no user id");
		alert("사용자 정보를 찾을 수 없습니다. 다시 시도해 주세요.");
		location.href = "/login";
	}

	const res = await fetch("/api/users/verify/password", {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			id: localStorage.getItem("user.id"),
			password: pw,
		}),
	});

	if (!res.ok) {
		console.error("비밀번호 인증 실패:", res.status);
		return res.text();
	}

	console.log("비밀번호 일치");
	return null;
}

function changePassword() {
	const pw = document.getElementById("pwInput").value;
	const pw2 = document.getElementById("pwConfirm").value;

	if (pw !== pw2) {
		alert("비밀번호를 다시 확인해주세요!");
		return;
	}

	if (localStorage.getItem("user.id") === null) {
		console.error("there's no user id");
		alert("사용자 정보를 찾을 수 없습니다. 다시 시도해 주세요.");
		location.href = "/login";
	}

	fetch("/api/users/info", {
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

			if (document.referrer.endsWith("/user/verify/password")) {
				// 사용자 설정
				alert("수정 사항이 반영되었습니다.");
				location.href = "/user/settings";
			} else {
				// 로그인 화면 (비번잊음)일 경우
				alert("새로운 비밀번호를 설정했습니다. 다시 로그인해 주세요.");
				localStorage.removeItem("user.id");
				location.href = "/login";
			}
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

	fetch("/api/users/info", {
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

			if (res.status === 200) {
				alert("변경 사항이 반영되었습니다.");
			} else {
				alert("변경 사항이 없습니다. 기존 페이지로 돌아갑니다.");
			}
			location.href = "/user/settings";
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

	fetch("/api/users/info", {
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

			if (res.status === 200) {
				alert("변경 사항이 반영되었습니다.");
			} else {
				alert("변경 사항이 없습니다. 기존 페이지로 돌아갑니다.");
			}
			location.href = "/user/settings";
		})
		.catch(async (err) => {
			console.error("수정 실패:", err.status);
			alert(await err.text());
		});
}

async function deleteAccount() {
	if (
		localStorage.getItem("user.id") === null ||
		localStorage.getItem("user.lastLogin") === null
	) {
		console.error("there's no user id");
		alert("사용자 정보를 찾을 수 없습니다. 다시 시도해 주세요.");
		location.href = "/login";
	}

	const msg = await verifyByPassword();

	if (msg !== null) {
		alert(msg);
		return;
	}

	const really = confirm(
		"정말 삭제를 진행하시겠습니까?\n삭제된 사용자의 정보는 복구할 수 없습니다."
	);
	if (!really) {
		location.href = "/user/settings";
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

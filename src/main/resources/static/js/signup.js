function signup() {
	const loginId = document.getElementById("idInput").value.trim() || "";
	const password = document.getElementById("pwInput").value.trim() || "";
	const nickname = document.getElementById("nickInput").value.trim() || "";
	const email = document.getElementById("emailInput").value.trim() || "";

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

function showPassword() {
	const pwInput = document.getElementById("pwInput");
	const showPassword = document.querySelector(".showPassword");

	if (showPassword.classList.contains("active")) {
		pwInput.setAttribute("type", "password");
	} else {
		pwInput.setAttribute("type", "text");
	}

	showPassword.classList.toggle("active");
}

function checkWordsCount(input) {
	const inputName = input.getAttribute("id");
	const labelName = inputName.replace("Input", "Length");
	const lb = document.getElementById(labelName);

	const content = lb.textContent.split(" / ");
	const limit = parseInt(content[1]);
	const currentCount = input.value.length;

	if (currentCount > limit) {
		lb.style.color = "red";
	}

	if (labelName === "idLength") {
		if (currentCount < 4) {
			lb.style.color = "red";
		}
	} else if (labelName === "pwLength") {
		if (currentCount < 8) {
			lb.style.color = "red";
		}
	}

	lb.textContent = toString(currentCount) + " / " + toString(limit);
}

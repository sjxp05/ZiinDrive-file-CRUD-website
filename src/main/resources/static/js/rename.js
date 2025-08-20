function renameFile(btn) {
	const tr = btn.closest("tr");
	const td = tr.querySelector("td:nth-child(1)");
	const id = tr.dataset.id;
	const fullName = tr.dataset.fullname;
	const currentName = td.textContent;

	let enterNoticed = false;

	// 중복 변경 방지
	if (td.querySelector("input")) return;

	// input 태그 삽입
	td.innerHTML = `<input type="text" value="${fullName}" onfocus="this.select() " style="text-align: center; font-size: 15px" />`;

	const input = td.querySelector("input");
	input.focus(); // focus 줘서 커서 바로 활성화되도록

	// 1. Enter 키 눌렀을 때
	input.addEventListener("keydown", function (e) {
		if (e.key === "Enter") {
			console.log("enter 이벤트 발동");
			enterNoticed = true;
			console.log("이벤트가 이미 감지됨");
			finalizeRename(id, td, input.value, currentName);
		}
	});

	// 2. 바깥 클릭 (포커스 잃었을 때)
	input.addEventListener("blur", function () {
		if (enterNoticed === false) {
			console.log("blur 이벤트 발동");
			finalizeRename(id, td, input.value, currentName);
		}
	});
}

function finalizeRename(id, td, newName, currentName) {
	newName = newName.trim();

	// 비어있는 경우
	if (newName === "") {
		td.textContent = currentName;
		return;
	}

	fetch("/api/files/" + id, {
		method: "PATCH",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			newName: newName,
		}),
	})
		.then((res) => {
			if (!res.ok) {
				throw res;
			}
			return res;
		})
		.then(async (res) => {
			if (res.status === 200) {
				const contentType = res.headers.get("Content-Type");
				if (contentType && contentType.includes("application/json")) {
					const renameInfo = await res.json();
					console.log("새 이름 + 풀네임 반영");

					td.closest("tr").dataset.fullname = renameInfo.fullName;
					td.textContent = renameInfo.truncatedName;
				}
			} else if (res.status === 204) {
				// 이름이 실질적으로 바뀌지 않은 경우
				console.log("이름이 같음", res.status);
				td.textContent = currentName;
			}
		})
		.catch(async (err) => {
			// 길이 제한 or 특수문자 제한 등 경고 메시지
			const msg = await err.text();
			alert(msg);

			console.error("이름 변경 실패:", msg);
			td.textContent = currentName;
		});
}

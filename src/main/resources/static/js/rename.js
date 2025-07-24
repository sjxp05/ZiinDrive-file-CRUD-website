import { renderData } from "./render.js";

function renameFile(btn) {
	const td = btn.closest("tr").querySelector("td:nth-child(1)");
	const id = btn.closest("tr").dataset.id;
	const currentName = td.textContent;

	let enterNoticed = false;

	// 중복 변경 방지
	if (td.querySelector("input")) return;

	// input 태그 삽입
	td.innerHTML = `<input type="text" value="${currentName}" onfocus="this.select() " style="text-align: center; font-size: 15px" />`;

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

	// 금지 문자 제한
	const invalidChars = /[<>:"/\\|?*]/;

	if (invalidChars.test(newName)) {
		td.textContent = currentName;
		alert(
			'파일명에는 다음과 같은 문자를 사용할 수 없습니다.\n< > : " / \\ | ? *'
		);
		return;
	}

	fetch("/api/files", {
		method: "PATCH",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			id: id,
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
				const fileList = await res.json();

				console.log("이름 변경 성공");
				renderData(fileList);
			} else {
				console.log("이름이 같음", res.status);
				td.textContent = currentName;
			}
		})
		.catch(async (err) => {
			// 길이 제한
			const msg = await err.text();
			alert(msg);

			console.error("이름 변경 실패:", msg);
			td.textContent = currentName;
		});
}

window.renameFile = renameFile;

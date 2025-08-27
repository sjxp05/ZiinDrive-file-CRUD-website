// 휴지통 열었을 때 데이터 렌더링
document.addEventListener("DOMContentLoaded", () => {
	const lastLogin = localStorage.getItem("user.lastLogin");
	const now = new Date();
	const threeMonthsAgo = now.setMonth(now.getMonth() - 3);

	if (
		localStorage.getItem("user.id") === null ||
		lastLogin === null ||
		lastLogin < threeMonthsAgo
	) {
		location.href = "/error";
	} else {
		localStorage.setItem("user.lastLogin", now);
	}

	// 파일 불러오기
	fetch("/api/files/" + localStorage.getItem("user.id"))
		.then((res) => {
			if (!res.ok) {
				throw new Error(res.status);
			}
			return res;
		})
		.then(async (res) => {
			if (res.status === 200) {
				const fileList = await res.json();

				console.log("all files fetch 성공");
				renderData(fileList);
			}
		})
		.catch((err) => {
			console.error("fetch 실패:", err);
			location.href = "/error";
		});
});

// 파일 복원
function restoreFile(btn) {
	const id = btn.closest("tr").dataset.id;

	fetch("/api/bin/" + id, {
		method: "PATCH",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			userId: localStorage.getItem("user.id"),
		}),
	})
		.then((res) => {
			if (!res.ok) {
				throw new Error(res.status);
			}
			return res;
		})
		.then(async (res) => {
			const fileList = await res.json();

			console.log("파일 복원 성공");
			alert("파일을 복원하였습니다.");
			renderData(fileList);
		})
		.catch((err) => {
			console.error("파일 복원 실패:", err);
		});
}

// 파일 하나만 영구삭제
function shredFile(btn) {
	const id = btn.closest("tr").dataset.id;

	// 정말 삭제할지 경고
	const really = confirm("정말 삭제하시겠습니까?");

	if (!really) {
		return;
	}

	fetch("/api/bin/" + localStorage.getItem("user.id") + "/" + id, {
		method: "DELETE",
	})
		.then((res) => {
			if (!res.ok) {
				throw new Error(res.status);
			}
			return res;
		})
		.then(async (res) => {
			const fileList = await res.json();

			console.log("파일 영구삭제 성공");
			alert("파일이 영구 삭제되었습니다.");
			renderData(fileList);
		})
		.catch((err) => {
			console.error("파일 영구삭제 실패:", err);
		});
}

// 휴지통 안의 파일 모두 영구삭제
function shredAll() {
	const trs = document.getElementById("tBody").querySelectorAll("tr");
	const trCount = trs.length;

	const really = confirm(
		"휴지통에 있는 " + trCount + " 개의 파일을 모두 삭제하시겠습니까?"
	);

	if (!really) {
		return;
	}

	fetch("/api/bin/" + localStorage.getItem("user.id"), {
		method: "DELETE",
	})
		.then((res) => {
			if (!res.ok) {
				throw new Error(res.status);
			}
			return res;
		})
		.then(async (res) => {
			const emptyList = await res.json();
			renderData(emptyList);
			console.log("휴지통 비우기 성공");
		})
		.catch((err) => {
			console.error("휴지통 비우기 실패:", err);
		});
}

// 데이터 렌더링 함수들
function renderData(fileList) {
	const tBody = document.getElementById("tBody");
	tBody.innerHTML = "";

	if (fileList.length === 0) {
		document.getElementById("isEmpty").style.display = "flex"; // '파일이 없습니다' 표시
		return;
	} else {
		document.getElementById("isEmpty").style.display = "none"; // 숨기기

		const tableRows = fileList.map((file) => createRow(file)).join("");
		tBody.innerHTML = tableRows;
	}
}

function createRow(file) {
	return `<tr
				data-id=${file.id}
				style="height: 40px; align-items: center; border-bottom: 1px solid #ccc"
			>
				<td>${file.truncatedName}</td>
				<td>${file.formattedDate}</td>
				<td>${file.size}</td>
				<td>
					<button onclick="restoreFile(this)" class="restoreBt">
						복원
					</button>
				</td>
				<td>
					<button onclick="shredFile(this)" class="shredBt">
						삭제
					</button>
				</td>
			</tr>`;
}

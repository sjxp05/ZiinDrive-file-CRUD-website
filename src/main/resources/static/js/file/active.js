// 검색어 입력창
const keyword = document.getElementById("keyword");
const extension = document.getElementById("extension");
const from = document.getElementById("from");
const to = document.getElementById("to");

// 정렬 버튼 동작 설정
const buttons = document.querySelectorAll(".sortBt");

buttons.forEach((btn) => {
	btn.addEventListener("click", () => {
		buttons.forEach((b) => {
			b.classList.remove("active");
		});

		btn.classList.add("active");

		setSort(btn.dataset.sort);
	});
});

// 시작 시 검색, 정렬조건 및 파일 불러오기
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

	// URL 파라미터에서 검색어 추출, 반영하기
	const params = new URLSearchParams(location.search);
	keyword.value = params.get("keyword") || "";
	extension.value = params.get("extension") || "";
	from.value = params.get("from") || "";
	to.value = params.get("to") || "";

	// 정렬 조건 반영하기
	fetch("/api/files/sort")
		.then((res) => {
			if (!res.ok) {
				throw new Error(res.status);
			}
			return res;
		})
		.then(async (res) => {
			const currentSort = await res.text();
			console.log("정렬조건 받기 완료:", currentSort);
			document.getElementById(currentSort).classList.add("active");
		})
		.catch((err) => {
			console.error("정렬조건 받기 실패:", err);
		});

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

// 검색 조건만 초기화
function initialize() {
	keyword.value = "";
	extension.value = "";
	from.value = "";
	to.value = "";
	console.log("검색 조건 초기화됨");
}

// 조건 검색
function searchFiles() {
	const rawParams = {
		keyword: keyword.value.trim(),
		extension: extension.value,
		from: from.value,
		to: to.value,
	};

	// 빈칸이 아닌 검색조건만 골라서 객체 형태로 저장
	const filteredParams = Object.entries(rawParams)
		.filter(([_, v]) => v != null && v !== "")
		.reduce((obj, [k, v]) => {
			obj[k] = v;
			return obj;
		}, {});

	if (Object.keys(filteredParams).length === 0) {
		// 검색어가 없을 때
		console.log("검색 조건이 비어 있음, 메인화면으로 돌아감");
		toMainView();
	} else {
		const query = new URLSearchParams(filteredParams).toString();

		if (location.href.startsWith("http://localhost:8080/favorites")) {
			location.href = "/favorites/search?" + query;
		} else {
			location.href = "/files/search?" + query;
		}
	}
}

// 정렬 조건 바꾸기
function setSort(sortOption) {
	fetch(
		"/api/files/" + localStorage.getItem("user.id") + "?sort=" + sortOption
	)
		.then((res) => {
			if (!res.ok) {
				throw new Error(res.status);
			}

			return res;
		})
		.then(async (res) => {
			if (res.status === 200) {
				const fileList = await res.json();

				console.log("정렬 성공:", sortOption);
				renderData(fileList);
			} else {
				console.log("정렬 필요 없음", res.status);
			}
		})
		.catch((err) => {
			console.error("정렬 실패:", err);
		});
}

// 파일 업로드
function uploadFile() {
	const input = document.getElementById("fileInput");
	const fileInput = input.files[0]; // 첫번째 파일 선택

	if (!fileInput) {
		console.log("파일이 선택되지 않음!");
		return;
	}

	// 폼 만들기
	const formData = new FormData();
	formData.append("fileInput", fileInput);
	formData.append("userId", localStorage.getItem("user.id"));

	fetch("/api/files", {
		method: "POST",
		body: formData,
	})
		.then((res) => {
			if (!res.ok) {
				throw res;
			}

			return res.status;
		})
		.then((status) => {
			if (status === 200) {
				location.href = "/files";
			} else {
				console.log("파일이 선택되지 않음");
			}
		})
		.catch(async (err) => {
			const msg = await err.text();
			alert(msg);
			console.error("업로드 실패:", msg);
		});
}

// 파일 다운로드
function downloadFile(btn) {
	const id = btn.closest("tr").dataset.id;

	fetch("/api/files/download/" + id)
		.then((res) => {
			if (!res.ok) {
				throw new Error(res.status);
			}

			const disposition = res.headers.get("Content-Disposition");

			// 파일명 추출하기
			let fileName = "";
			if (disposition && disposition.includes("filename=")) {
				fileName = disposition
					.split("filename=")[1]
					.replaceAll('"', "")
					.trim();
			}

			return res.blob().then((blob) => ({ blob, fileName }));
		})
		.then(({ blob, fileName }) => {
			const url = window.URL.createObjectURL(blob);
			const a = document.createElement("a");
			a.href = url;

			a.download = fileName;
			a.click();

			a.remove();
			window.URL.revokeObjectURL(url);

			console.log("다운로드 성공");
		})
		.catch((err) => {
			console.error("다운로드 실패:", err);
		});
}

// 이름 변경 버튼을 눌렀을 때
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

// 이름 변경 동작 최종 수행
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
			userId: localStorage.getItem("user.id"),
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

// 하트 버튼 눌렀을때
function favoriteFile(btn) {
	const id = btn.closest("tr").dataset.id;

	if (location.href.startsWith("http://localhost:8080/favorites")) {
		//즐겨찾기 모음이면 즉시 반영될 수 있도록 다음 함수로 이동
		reloadFavoriteList(id);
	} else {
		// 바뀌었다는 정보 전달 + 해당 파일의 표시만 바꿔 반영해주기
		const change = btn.innerText === "\u00A0♡\u00A0" ? true : false;

		fetch("/api/files/favorite/" + id, {
			method: "PATCH",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify({
				userId: localStorage.getItem("user.id"),
				change: change,
			}),
		})
			.then((res) => {
				if (!res.ok) {
					throw new Error(res.status);
				}
			})
			.then(() => {
				console.log("즐겨찾기 반영 성공");

				if (change === true) {
					btn.innerText = "\u00A0♥\u00A0";
					console.log("즐겨찾기 추가");
				} else {
					btn.innerText = "\u00A0♡\u00A0";
					console.log("즐겨찾기 해제");
				}
			})
			.catch((err) => {
				console.error("반영 실패:", err);
			});
	}
}

// 즐겨찾기 모음 화면에서는 삭제 시 실시간 반영되도록 하기
function reloadFavoriteList(id) {
	fetch("/api/favorites/" + id, {
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
			renderData(fileList);
			console.log("즐겨찾기 목록 다시 불러오기 성공");
		})
		.catch((err) => {
			console.error("불러오기 실패:", err);
		});
}

// 파일 1개 삭제
function deleteFile(btn) {
	const id = btn.closest("tr").dataset.id;

	fetch("/api/files/" + localStorage.getItem("user.id") + "/" + id, {
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

			console.log("파일 삭제 완료");
			alert("휴지통으로 이동하였습니다.");
			renderData(fileList);
		})
		.catch((err) => {
			console.error("파일 삭제 실패:", err);
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
				data-fullname="${file.originalName}"
				style="height: 40px; align-items: center; border-bottom: 1px solid #ccc"
			>
				<td>${file.truncatedName}</td>
				<td>${file.formattedDate}</td>
				<td>${file.size}</td>
				<td style="color: red; cursor: pointer" onclick="favoriteFile(this)">
					${file.favorited ? "&nbsp;♥&nbsp;" : "&nbsp;♡&nbsp;"}
				</td>
				<td>
					<button
						onclick="downloadFile(this)"
						class="downloadBt"
					>
						다운로드
					</button>
				</td>
				<td>
					<button onclick="renameFile(this)" class="renameBt">
						이름 변경
					</button>
				</td>
				<td>
					<button onclick="deleteFile(this)" class="deleteBt">
						삭제
					</button>
				</td>
			</tr>`;
}

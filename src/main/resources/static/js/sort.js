const div = document.getElementById("fileTable");
const buttons = document.querySelectorAll(".sortBt");

addEventListener("DOMContentLoaded", () => {
	const currentSort = div.dataset.sort;
	document.getElementById(currentSort).classList.add("active");
});

buttons.forEach((btn) => {
	btn.addEventListener("click", () => {
		buttons.forEach((b) => {
			b.classList.remove("active");
		});

		btn.classList.add("active");

		setSort(btn.dataset.sort);
	});
});

function setSort(sortOption) {
	fetch("/api/files?sort=" + sortOption)
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
				// location.reload(); // 페이지 새로고침하면서 데이터도 새로 반영됨! <-- 얘를 리로딩x 리렌더링으로 바꾸려고 생각중
				renderData(fileList);
			} else {
				console.log("정렬 필요 없음", res.status);
			}
		})
		.catch((err) => {
			console.error("정렬 실패:", err);
		});
}

function renderData(fileList) {
	const tBody = document.getElementById("tBody");
	tBody.innerHTML = "";

	if (fileList.length === 0) {
		document.getElementById("isEmpty").style.display = "flex";
		return;
	} else {
		document.getElementById("isEmpty").style.display = "none";

		const tableRows = fileList.map((file) => createTableRow(file)).join("");
		tBody.innerHTML = tableRows;
	}
}

function createTableRow(file) {
	return `<tr
				th:attr="data-id=${file.id}"
				style="height: 40px; align-items: center"
			>
				<td>${file.originalName}</td>
				<td>${file.formattedDate}</td>
				<td>${file.size}</td>
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

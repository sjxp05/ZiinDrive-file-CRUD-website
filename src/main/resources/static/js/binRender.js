document.addEventListener("DOMContentLoaded", () => {
	// 파일 불러오기
	fetch("/api/files")
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
		});
});

export function renderData(fileList) {
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
				style="height: 40px; align-items: center"
			>
				<td>${file.originalName}</td>
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

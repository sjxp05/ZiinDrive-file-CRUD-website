export function renderData(fileList) {
	const tBody = document.getElementById("tBody");
	tBody.innerHTML = "";

	if (fileList.length === 0) {
		document.getElementById("isEmpty").style.display = "flex";
		return;
	} else {
		document.getElementById("isEmpty").style.display = "none";

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

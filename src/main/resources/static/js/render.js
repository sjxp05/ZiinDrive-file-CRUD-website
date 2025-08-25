document.addEventListener("DOMContentLoaded", () => {
	const lastLogin = localStorage.getItem("user.lastLogin");
	const now = new Date();
	const threeMonthsAgo = now.setMonth(now.getMonth() - 3);

	if (
		localStorage.getItem("user.id") === null ||
		lastLogin === null ||
		lastLogin < threeMonthsAgo
	) {
		location.href = "/login/error";
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
				data-fullname="${file.originalName}"
				style="height: 40px; align-items: center"
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

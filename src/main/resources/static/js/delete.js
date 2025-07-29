import { renderData } from "./render.js";

function deleteFile(btn) {
	const id = btn.closest("tr").dataset.id;

	fetch("/api/files/" + id, {
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

window.deleteFile = deleteFile;

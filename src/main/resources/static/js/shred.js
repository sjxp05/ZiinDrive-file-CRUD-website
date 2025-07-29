import { renderData } from "./binRender.js";

function shredFile(btn) {
	const id = btn.closest("tr").dataset.id;

	// 정말 삭제할지 경고
	const really = confirm("정말 삭제하시겠습니까?");

	if (!really) {
		return;
	}

	fetch("/api/bin/" + id, {
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

window.shredFile = shredFile;

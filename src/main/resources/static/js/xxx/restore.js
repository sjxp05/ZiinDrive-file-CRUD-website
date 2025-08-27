import { renderData } from "./binRender.js";

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

window.restoreFile = restoreFile;

function deleteFile(btn) {
	const id = btn.closest("tr").dataset.id;

	// 정말 삭제할지 경고
	const really = confirm("정말 삭제하시겠습니까?");

	if (!really) {
		console.log("삭제 취소");
		return;
	}

	fetch("/api/files/" + id, {
		method: "DELETE",
	})
		.then((res) => {
			if (!res.ok) {
				throw new Error(res.status);
			}
		})
		.then(() => {
			console.log("파일 삭제 완료");
			location.reload();
		})
		.catch((err) => {
			console.error("파일 삭제 실패:", err);
		});
}

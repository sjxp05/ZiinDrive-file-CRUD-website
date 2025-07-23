function uploadFile() {
	const input = document.getElementById("fileInput");
	const fileInput = input.files[0]; // 첫번째 파일

	if (!fileInput) {
		console.log("파일이 선택되지 않음!");
		return;
	}

	// 폼 만들기
	const formData = new FormData();
	formData.append("fileInput", fileInput);

	fetch("/api/files", {
		method: "POST",
		body: formData,
	})
		.then((res) => {
			if (!res.ok) {
				throw new Error(res.status);
			}
		})
		.then(() => {
			console.log("업로드 성공");
			location.href = "/files";
		})
		.catch((err) => {
			console.error("업로드 실패:", err);
		});
}

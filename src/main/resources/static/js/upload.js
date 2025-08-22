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

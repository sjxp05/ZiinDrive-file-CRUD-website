function downloadFile(btn) {
	const id = btn.closest("tr").dataset.id;

	fetch("/api/files/" + id)
		.then((res) => {
			if (!res.ok) {
				throw new Error(res.status);
			}

			const disposition = res.headers.get("Content-Disposition");

			// 파일명 추출하기
			let fileName = "";
			if (disposition && disposition.includes("filename=")) {
				fileName = disposition
					.split("filename=")[1]
					.replaceAll('"', "")
					.trim();
			}

			return res.blob().then((blob) => ({ blob, fileName }));
		})
		.then(({ blob, fileName }) => {
			const url = window.URL.createObjectURL(blob);
			const a = document.createElement("a");
			a.href = url;

			a.download = fileName;
			a.click();

			a.remove();
			window.URL.revokeObjectURL(url);

			console.log("다운로드 성공");
		})
		.catch((err) => {
			console.error("다운로드 실패:", err);
		});
}

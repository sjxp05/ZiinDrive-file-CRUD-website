const div = document.getElementById("fileTable");
const buttons = document.querySelectorAll(".sortBt");

addEventListener("DOMContentLoaded", () => {
	const currentSort = div.dataset.sort;
	document.getElementById(currentSort).classList.add("active");
});

buttons.forEach((btn) => {
	btn.addEventListener("click", () => {
		setSort(btn.dataset.sort);
	});
});

function setSort(sortOption) {
	fetch("/api/files?sort=" + sortOption)
		.then((res) => {
			if (!res.ok) {
				throw new Error(res.status);
			}

			return res.status;
		})
		.then((status) => {
			if (status === 200) {
				console.log("정렬 성공:", sortOption);
				location.reload(); // 페이지 새로고침하면서 데이터도 새로 반영됨!
			} else {
				console.log("정렬 필요 없음", status);
			}
		})
		.catch((err) => {
			console.error("정렬 실패:", err);
		});
}

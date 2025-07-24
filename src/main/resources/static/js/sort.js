import { renderData } from "./render.js";

const div = document.getElementById("fileTable");
const buttons = document.querySelectorAll(".sortBt");

addEventListener("DOMContentLoaded", () => {
	// 정렬 조건 반영하기
	fetch("/api/files/sort")
		.then((res) => {
			if (!res.ok) {
				throw new Error(res.status);
			}
			return res;
		})
		.then(async (res) => {
			const currentSort = await res.text();
			console.log("정렬조건 받기 완료:", currentSort);

			div.dataset.sort = currentSort;
			document.getElementById(currentSort).classList.add("active");
		})
		.catch((err) => {
			console.error("정렬조건 받기 실패:", err);
		});
});

buttons.forEach((btn) => {
	btn.addEventListener("click", () => {
		buttons.forEach((b) => {
			b.classList.remove("active");
		});

		btn.classList.add("active");

		setSort(btn.dataset.sort);
	});
});

function setSort(sortOption) {
	fetch("/api/files/sort/" + sortOption)
		.then((res) => {
			if (!res.ok) {
				throw new Error(res.status);
			}

			return res;
		})
		.then(async (res) => {
			if (res.status === 200) {
				const fileList = await res.json();

				console.log("정렬 성공:", sortOption);
				// location.reload(); // 페이지 새로고침하면서 데이터도 새로 반영됨! <-- 얘를 리로딩x 리렌더링으로 바꾸려고 생각중
				renderData(fileList);
			} else {
				console.log("정렬 필요 없음", res.status);
			}
		})
		.catch((err) => {
			console.error("정렬 실패:", err);
		});
}

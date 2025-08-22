import { renderData } from "./render.js";

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
	fetch(
		"/api/files/" + localStorage.getItem("user.id") + "?sort=" + sortOption
	)
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
				renderData(fileList);
			} else {
				console.log("정렬 필요 없음", res.status);
			}
		})
		.catch((err) => {
			console.error("정렬 실패:", err);
		});
}

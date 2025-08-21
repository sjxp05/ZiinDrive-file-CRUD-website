import { renderData } from "./render.js";

// 하트 버튼 눌렀을때
function favoriteFile(btn) {
	const id = btn.closest("tr").dataset.id;

	if (
		location.href.endsWith("/favorites") ||
		location.href.endsWith("/favorites/search")
	) {
		//즐겨찾기 모음이면 즉시 반영될 수 있도록 다음 함수로 이동
		reloadFavoriteList(id, false);
	} else {
		// 바뀌었다는 정보 전달 + 해당 파일의 표시만 바꿔 반영해주기
		const change = btn.innerText === "\u00A0♡\u00A0" ? true : false;

		fetch("/api/files/favorite/" + id, {
			method: "PATCH",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify({
				change: change,
			}),
		})
			.then((res) => {
				if (!res.ok) {
					throw new Error(res.status);
				}
			})
			.then(() => {
				console.log("즐겨찾기 반영 성공");

				if (change === true) {
					btn.innerText = "\u00A0♥\u00A0";
					console.log("즐겨찾기 추가");
				} else {
					btn.innerText = "\u00A0♡\u00A0";
					console.log("즐겨찾기 해제");
				}
			})
			.catch((err) => {
				console.error("반영 실패:", err);
			});
	}
}

// 즐겨찾기 모음 화면에서는 삭제 시 실시간 반영되도록 하기
function reloadFavoriteList(id) {
	fetch("/api/favorites/" + id, {
		method: "PATCH",
	})
		.then((res) => {
			if (!res.ok) {
				throw new Error(res.status);
			}
			return res;
		})
		.then(async (res) => {
			const fileList = await res.json();
			renderData(fileList);
			console.log("즐겨찾기 목록 다시 불러오기 성공");
		})
		.catch((err) => {
			console.error("불러오기 실패:", err);
		});
}

window.favoriteFile = favoriteFile;

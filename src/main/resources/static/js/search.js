import { renderData } from "./render.js";

const keyword = document.getElementById("keyword");
const extension = document.getElementById("extension");
const from = document.getElementById("from");
const to = document.getElementById("to");

document.addEventListener("DOMContentLoaded", () => {
	const params = new URLSearchParams(location.search);

	keyword.value = params.get("keyword") || "";
	extension.value = params.get("extension") || "";
	from.value = params.get("from") || "";
	to.value = params.get("to") || "";

	console.log("검색조건 화면 반영 완료");

	// 파일 불러오기
	fetch("/api/files")
		.then((res) => {
			if (!res.ok) {
				throw new Error(res.status);
			}
			return res;
		})
		.then(async (res) => {
			if (res.status === 200) {
				const fileList = await res.json();

				console.log("all files fetch 성공");
				renderData(fileList);
			}
		})
		.catch((err) => {
			console.error("fetch 실패:", err);
		});
});

function initialize() {
	if (location.href === "http://localhost:8080/files") {
		keyword.value = "";
		extension.value = "";
		from.value = "";
		to.value = "";

		console.log("메인 화면 유지");
		return;
	} else {
		location.href = "/files";
	}
}

function searchFiles() {
	const rawParams = {
		keyword: keyword.value.trim(),
		extension: extension.value,
		from: from.value,
		to: to.value,
	};

	const filteredParams = Object.entries(rawParams)
		.filter(([_, v]) => v != null && v !== "")
		.reduce((obj, [k, v]) => {
			obj[k] = v;
			return obj;
		}, {});

	if (Object.keys(filteredParams).length === 0) {
		console.log("검색 조건이 비어 있음!");
		return;
	} else {
		console.log(filteredParams); // test

		const query = new URLSearchParams(filteredParams).toString();
		location.href = "/files/search?" + query;
	}
}

window.initialize = initialize;
window.searchFiles = searchFiles;

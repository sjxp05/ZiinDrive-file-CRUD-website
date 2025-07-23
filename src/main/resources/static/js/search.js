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
});

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

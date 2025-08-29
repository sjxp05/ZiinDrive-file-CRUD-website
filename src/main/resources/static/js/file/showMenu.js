const userPage = document.getElementById("userPage");
const btn = document.getElementById("profile");
const sidemenu = document.getElementById("sidemenu");
const overlay = document.getElementById("overlay");

document.addEventListener("click", (e) => {
	if (e.target === btn && !userPage.classList.contains("active")) {
		// 버튼을 눌렀고 사용자페이지가 안 떠있을 경우
		userPage.classList.add("active");

		fetch("/api/users/profile/" + localStorage.getItem("user.id"))
			.then((res) => {
				if (!res.ok) {
					throw res;
				}
				return res;
			})
			.then(async (data) => {
				const profileInfo = await data.json();

				document.getElementById("pfNickname").textContent =
					profileInfo.nickname;
				document.getElementById("pfId").textContent =
					profileInfo.loginId;
			})
			.catch((err) => {
				console.error("사용자 정보 불러오기 실패:", err.status);
			});
	} else if (userPage.contains(e.target)) {
		// userPage 안쪽의 공간 or 요소를 클릭한 경우
		return;
	} else {
		userPage.classList.remove("active");
	}
});

function showMenu() {
	overlay.classList.add("active");
	sidemenu.classList.add("active");

	if (location.href.startsWith("http://localhost:8080/favorites")) {
		document.getElementById("favoritesMenu").classList.add("active");
	} else if (location.href.startsWith("http://localhost:8080/bin")) {
		document.getElementById("trashbinMenu").classList.add("active");
	} else {
		document.getElementById("allFilesMenu").classList.add("active");
	}
}

function closeMenu() {
	overlay.classList.remove("active");
	sidemenu.classList.remove("active");
	userPage.classList.remove("active");
}

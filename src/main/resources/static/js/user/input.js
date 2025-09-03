function showPassword(btn) {
	const pwInput = btn.previousElementSibling;

	if (btn.classList.contains("active")) {
		pwInput.type = "password";
	} else {
		pwInput.type = "text";
	}

	btn.classList.toggle("active");
}

function checkIfSamePw() {
	const pwInput = document.getElementById("pwInput").value;
	const confInput = document.getElementById("pwConfirm");
	const pwConfirm = confInput.value;

	if (pwInput !== pwConfirm) {
		confInput.style.borderColor = "red";
	} else {
		confInput.style.borderColor = "darkgray";
	}
}

function checkWordsCount(input) {
	const inputName = input.id;
	const labelName = inputName.replace("Input", "Length");
	const lb = document.getElementById(labelName);

	const content = lb.textContent.split(" / ");
	const limit = parseInt(content[1]);
	const currentCount = input.value.length;

	if (currentCount > limit) {
		lb.style.color = "red";
	} else {
		lb.style.color = "gray";

		if (labelName === "idLength") {
			if (currentCount < 4) {
				lb.style.color = "red";
			}
		} else if (labelName === "pwLength") {
			if (currentCount < 8 || currentCount > limit) {
				lb.style.color = "red";
			}
		}
	}

	lb.textContent = String(currentCount) + " / " + String(limit);
}

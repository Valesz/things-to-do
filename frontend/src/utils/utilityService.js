const dateFormat = { year: "numeric", month: "numeric", day: "numeric" };

export function formatDate(date) {
	return date.toLocaleDateString("hu-HU", dateFormat).replaceAll(". ", "-").replace(".", "");
}

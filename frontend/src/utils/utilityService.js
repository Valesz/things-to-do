const dateFormat = { year: "numeric", month: "numeric", day: "numeric" };

export function formatDate(date) {
	return date.toLocaleDateString("hu-HU", dateFormat).replaceAll(". ", "-").replace(".", "");
}

export function makeKeywordObjectForBackEnd(keywordList, taskId) {
	return keywordList.map(keyword => {
		return {
			keyword: keyword,
			taskid: taskId,
		}
	})
}

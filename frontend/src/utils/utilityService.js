const dateFormat = {year: 'numeric', month: 'numeric', day: 'numeric'}

export function formatDate(date) {
	return new Date(date).toLocaleString('hu-HU', dateFormat).replaceAll('. ', '-').replace('.', '')
}

export function makeKeywordObjectForBackEnd(keywordList, taskId) {
	return keywordList.map(keyword => {
		return {
			keyword: keyword,
			taskid: taskId
		}
	})
}

export const setInvalidAttributeToFromElements = (formData, setFormData, fieldNamesWithInvalidStatus) => {
	let valami = formData

	for (const property in fieldNamesWithInvalidStatus) {
		valami = {
			...valami,
			[property]: {
				...formData[property],
				invalid: fieldNamesWithInvalidStatus[property].invalid
			}
		}
	}

	setFormData({...valami})
}

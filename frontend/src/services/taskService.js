import {formatDate, makeKeywordObjectForBackEnd} from '../utils/utilityService'
import {fetchJson, fetchText} from './fetchService'
import {addKeywords, deleteKeywords} from './keywordService'

const baseEndpoint = '/api/task/'

export async function fetchTasks({id, name, keywords, date, completed, completedUserId, ownerId, creatorName} = {}) {

	const requestOptions = {
		method: 'GET',
		headers: {
			'Content-Type': 'application/json'
		}
	}

	const params = '?1=1' +
		(id ? '&id=' + id : '') +
		(name && name !== '' ? '&name=' + name : '') +
		(keywords && keywords.length > 0 ? '&keywords=' + keywords.join('&keywords=') : '') +
		((date &&
				(date[0] !== null ? '&createdafter=' + formatDate(date[0]) : '') +
				(date[1] !== null ? '&createdbefore=' + formatDate(date[1]) : ''))
			|| '') +
		(completed !== undefined ? '&completed=' + completed : '') +
		(completedUserId ? `&completeduserid=${completedUserId}` : '') +
		(creatorName ? '&ownername=' + creatorName : '') +
		(ownerId ? '&ownerid=' + ownerId : '')

	return await fetchJson(baseEndpoint + params, requestOptions)
}

export async function addTasks({authToken, name, description, mainTaskId = null, ownerId, timeofcreation, keywords} = {}) {

	if (authToken === undefined) {
		return undefined
	}

	const requestOptions = {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			'Authorization': 'Bearer ' + authToken
		},
		body: JSON.stringify({
			name: name,
			description: description,
			mainTaskId: mainTaskId,
			ownerid: ownerId,
			timeofcreation: formatDate(timeofcreation)
		})
	}

	return await fetchJson(baseEndpoint, requestOptions)
		.then(async task => {
			if (keywords) {
				await addKeywords(authToken, makeKeywordObjectForBackEnd(keywords, task.id))
					.then(keywords => {
						task.keywords = keywords.map(keyword => keyword.keyword)
					})
					.catch(async error => {
						console.log(error)
						return await Promise.reject(new Error(error))
					})
			}

			return await Promise.resolve(task)
		})
}

export async function updateTask({authToken, id, name, description, mainTaskId, ownerId, timeofcreation, keywords}) {
	if (!authToken || !id) {
		return undefined
	}

	const requestOptions = {
		method: 'PUT',
		headers: {
			'content-type': 'application/json',
			'Authorization': 'Bearer ' + authToken
		},
		body: JSON.stringify({
			id: id,
			name: name,
			description: description,
			mainTaskId: mainTaskId,
			ownerId: ownerId,
			timeofcreation: timeofcreation
		})
	}

	return await fetchJson(baseEndpoint, requestOptions)
		.then(async task => {
			if (keywords) {
				const res = await deleteKeywords(authToken, id)
					.catch(async error => {
						console.log(error)
						return await Promise.reject(new Error(error))
					})

				if (res) {
					return await Promise.reject(new Error(res))
				}

				await addKeywords(authToken, makeKeywordObjectForBackEnd(keywords, task.id))
					.then(keywords => {
						task.keywords = keywords.map(keyword => keyword.keyword)
					})
					.catch(async error => {
						console.log(error)
						return await Promise.reject(new Error(error))
					})
			}

			return await Promise.resolve(task)
		})
}

export async function deleteTask({authToken, id} = {}) {
	if (authToken === undefined) {
		return undefined
	}

	const requestOptions = {
		method: 'DELETE',
		headers: {
			'Authorization': 'Bearer ' + authToken
		}
	}

	return await fetchText(baseEndpoint + `${id}`, requestOptions)
}

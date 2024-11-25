import {fetchJson, fetchText} from './fetchService'

const baseEndpoint = '/api/task/keyword/'

export async function addKeywords(authToken, keywordsList) {
	const requestOptions = {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			'Authorization': 'Bearer ' + authToken
		},
		body: JSON.stringify(keywordsList)
	}

	return await fetchJson(baseEndpoint, requestOptions)
}

export async function deleteKeywords(authToken, taskId) {
	const requestOptions = {
		method: 'DELETE',
		headers: {
			'Content-Type': 'application/json',
			'Authorization': 'Bearer ' + authToken
		}
	}

	return await fetchText(`/api/task/${taskId}/keyword`, requestOptions)
}
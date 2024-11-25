import {fetchJson, fetchText} from './fetchService'
import {formatDate} from '../utils/utilityService'

const baseEndpoint = '/api/submission/'

export async function addSubmission({authToken, taskId, description, timeOfSubmission, acceptance, submitterId}) {
	if (authToken === undefined) {
		return undefined
	}

	const requestOptions = {
		method: 'POST',
		headers: {
			'content-type': 'application/json',
			'Authorization': 'Bearer ' + authToken
		},
		body: JSON.stringify({
			taskid: taskId,
			description: description,
			timeofsubmission: timeOfSubmission,
			acceptance: acceptance,
			submitterid: submitterId
		})
	}

	return await fetchJson(baseEndpoint, requestOptions)
}

export async function fetchSubmission({id, taskId, description, timeOfSubmission, acceptance, submitterId}) {
	const requestOptions = {
		method: 'GET',
		headers: {
			'Content-Type': 'application/json'
		}
	}

	const params = '?1=1' +
		(id ? `&id=${id}` : '') +
		(taskId ? `&taskid=${taskId}` : '') +
		(description ? `&description=${description}` : '') +
		(timeOfSubmission ? `&timeofsubmission=${formatDate(timeOfSubmission)}` : '') +
		(acceptance ? `&acceptance=${acceptance}` : '') +
		(submitterId ? `&submitterId=${submitterId}` : '')

	return await fetchJson(baseEndpoint + params, requestOptions)
}

export async function modifySubmission({authToken, id, taskId, description, timeOfSubmission, acceptance, submitterId}) {
	if (!authToken) {
		return undefined
	}

	const requestOptions = {
		method: 'PUT',
		headers: {
			'Content-Type': 'application/json',
			'Authorization': `Bearer ${authToken}`
		},
		body: JSON.stringify({
			id: id,
			taskid: taskId,
			description: description,
			timeofsubmission: timeOfSubmission,
			acceptance: acceptance,
			submitterId: submitterId
		})
	}

	return await fetchJson(baseEndpoint, requestOptions)
}

export async function deleteSubmission(token, id) {
	if (!token) {
		return undefined
	}

	const requestOptions = {
		method: 'DELETE',
		headers: {
			'Authorization': `Bearer ${token}`
		}
	}

	return await fetchText(baseEndpoint + id, requestOptions)
}

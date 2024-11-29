import {formatDate} from '../../../utils/utilityService'
import {fetchJson, fetchText} from '../../../services/fetchService'

const baseEndpoint = '/api/user/'

export async function fetchUser({id, username, email, timeOfCreation, status, classification, precisionOfAnswers} = {}) {
	const params = '?1=1' +
		(id ? '&id=' + id : '') +
		(username && username !== '' ? '&username=' + username : '') +
		(email && email !== '' ? '&email=' + email : '') +
		((timeOfCreation &&
				(timeOfCreation[0] !== null ? '&createdafter=' + formatDate(timeOfCreation[0]) : '') +
				(timeOfCreation[1] !== null ? '&createdbefore=' + formatDate(timeOfCreation[1]) : ''))
			|| '') +
		(status && status !== '' ? '&status=' + status : '') +
		(classification ? '&classification=' + classification : '') +
		(precisionOfAnswers ? '&precisionofanswers=' + precisionOfAnswers : '')

	const requestOptions = {
		method: 'GET',
		headers: {
			'content-type': 'application/json'
		}
	}

	return await fetchJson(baseEndpoint + params, requestOptions)
}

export async function fetchUserByAuthToken(authToken) {
	const requestOptions = {
		method: 'GET',
		headers: {
			'content-type': 'application/json',
			'Authorization': 'Bearer ' + authToken
		}
	}

	return await fetchJson(baseEndpoint + 'token', requestOptions)
}

export async function updateUser({authToken, id, username, email, password, timeOfCreation, status, classification, precisionOfAnswers}) {
	if (!authToken) {
		throw new Error('AuthToken not given', {cause: 'AuthToken not given'})
	}

	const requestOptions = {
		method: 'PUT',
		headers: {
			'content-type': 'application/json',
			'Authorization': `Bearer ${authToken}`
		},
		body: JSON.stringify({
			id: id,
			username: username,
			email: email,
			password: password,
			timeofcreation: timeOfCreation,
			status: status,
			classification: classification,
			precisionOfAnswers: precisionOfAnswers
		})
	}

	return await fetchJson(baseEndpoint, requestOptions)
}

export async function deleteUser({authToken, id}) {
	const requestOptions = {
		method: 'DELETE',
		headers: {
			'content-type': 'application/json',
			'Authorization': `Bearer ${authToken}`
		}
	}

	return await fetchText(baseEndpoint + id, requestOptions)
}

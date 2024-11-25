import {fetchJson, fetchText} from './fetchService'

const baseEndpoint = '/api/auth'

export async function fetchJWTToken(username, password) {
	const requestOptions = {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			username: username,
			password: password
		})
	}

	return await fetchText(baseEndpoint + '/login', requestOptions)
}

export async function register(username, email, password) {
	const requestOptions = {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			username: username,
			email: email,
			timeofcreation: new Date().toLocaleDateString('en-CA'),
			status: 'AKTIV',
			password: password
		})
	}

	return await fetchJson(baseEndpoint + '/register', requestOptions)
}
import {serverEndpoint} from '../config/server-properties'

const baseEndpoint = "/api/auth";

export async function login(username, password) {
	const requestOptions = {
		method: "POST",
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify({
			username: username,
			password: password
		})
	};

	return await fetch(serverEndpoint + baseEndpoint + "/login", requestOptions)
		.then(async response => {
			const isText = response.headers.get('content-type')?.includes('text/plain');
			const data = isText && await response.text();

			if (!response.ok) {
				const error = (data && data.message) || response.status;
				return await Promise.reject(error);
			}

			if (data) {
				return await Promise.resolve(data);
			}
		});
}

export async function register(username, email, password) {
	const requestOptions = {
		method: "POST",
		headers: {
			"Content-Type" : "application/json",
		},
		body: JSON.stringify({
			username: username,
			email: email,
			timeofcreation: new Date().toLocaleDateString("en-CA"),
			status: "AKTIV",
			password: password,
		})
	};

	return await fetch(serverEndpoint + baseEndpoint + "/register", requestOptions)
		.then(async response => {
			const isJson = response.headers.get("content-type")?.includes("application/json");
			const data = isJson && await response.json();

			if (!response.ok) {
				const error = (data && data.message) || response.status;
				return await Promise.reject(error);
			}

			if (data) {
				return await Promise.resolve(data);
			}
		});
}
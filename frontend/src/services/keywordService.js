import {serverEndpoint} from '../config/server-properties'

const baseEndpoint = "/api/task/keyword/";

export async function addKeywords(authToken, keywordsList) {
	const requestOptionsKeywords = {
		method: "POST",
		headers: {
			'Content-Type': 'application/json',
			'Authorization': "Bearer " + authToken,
		},
		body: JSON.stringify(keywordsList)
	}

	return await fetch(serverEndpoint + baseEndpoint, requestOptionsKeywords)
		.then(async (response) => {
			const isJson = response.headers.get("content-type")?.includes("application/json");
			const data = isJson && await response.json();

			if (response.status !== 201) {
				const error = (data && data.message) || response.status;
				return await Promise.reject(error);
			}

			if (data) {
				return await Promise.resolve(data);
			}
		})
}
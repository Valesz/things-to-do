import {serverEndpoint} from '../config/server-properties'
import {formatDate} from '../utils/utilityService'

const baseEndpoint = "/api/task/";

export async function fetchTasks(name, keywords, date, completed, creatorName) {

	const requestOptions = {
		method: "GET",
		headers: {
			'Content-Type': 'application/json',
		},
	};

	const params = "?1=1" +
		(name && name !== "" ? "&name=" + name : "") +
		(keywords && keywords.length > 0 ? "&keywords=" + keywords.join("&keywords=") : "") +
		(date ?
			(date[0] !== null ? "&createdafter=" + formatDate(date[0]) : "") +
			(date[1] !== null ? "&createdbefore=" + formatDate(date[1]) : "")
			: "") +
		(completed ? "&completed=" + completed : "") +
		(creatorName ? "&ownername=" + creatorName : "");

	return await fetch(serverEndpoint + baseEndpoint + params, requestOptions)
		.then(async (response) => {
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

export async function addTasks(authToken, name, description, mainTaskId = null, timeofcreation) {

	if (authToken === undefined) {
		return undefined;
	}

	const requestOptionsTask = {
		method: "POST",
		headers: {
			'Content-Type': 'application/json',
			'Authorization': "Bearer " + authToken,
		},
		body: JSON.stringify({
			name: name,
			description: description,
			mainTaskId: mainTaskId,
			timeofcreation: formatDate(timeofcreation),
		})
	};

	return await fetch(serverEndpoint + baseEndpoint, requestOptionsTask)
		.then(async (response) => {
			const isJson = response.headers.get("content-type")?.includes("application/json");
			const task = isJson && await response.json();

			if (response.status !== 201) {
				const error = (task && task.message) || response.status;
				return await Promise.reject(error);
			}

			if (task) {
				return await Promise.resolve(task);
			}
		});
}

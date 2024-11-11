import {formatDate} from '../utils/utilityService'
import {fetchJson} from './fetchService'

const baseEndpoint = "/api/user/";

export async function fetchUser({id, username, email, timeOfCreation, status, classification, precisionOfAnswers} = {}) {
	const params = "?1=1" +
		(id ? "&id=" + id : "") +
		(username && username !== "" ? "&username=" + username : "") +
		(email && email !== "" ? "&email=" + email : "") +
		(timeOfCreation ?
			(timeOfCreation[0] !== null ? "&createdafter=" + formatDate(timeOfCreation[0]) : "") +
			(timeOfCreation[1] !== null ? "&createdbefore=" + formatDate(timeOfCreation[1]) : "")
			: "") +
		(status && status !== "" ? "&status=" + status : "") +
		(classification ? "&classification=" + classification : "") +
		(precisionOfAnswers ? "&precisionofanswers=" + precisionOfAnswers : "");

	const requestOptions = {
		method: "GET",
		headers: {
			"content-type" : "application/json",
		}
	}

	return await fetchJson(baseEndpoint + params, requestOptions);
}

export async function fetchUserByAuthToken(authToken) {
	const requestOptions = {
		method: "GET",
		headers: {
			"content-type": "application/json",
			"Authorization" : "Bearer " + authToken
		}
	}

	return await fetchJson(baseEndpoint + "token", requestOptions);
}

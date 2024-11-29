import {serverEndpoint} from '../config/server-properties'

export async function fetchJson(relativeEndpoint, requestOptions) {
	return await fetch(serverEndpoint + relativeEndpoint, requestOptions)
		.then(async (response) => {
			const isJson = response.headers.get('content-type')?.includes('application/json')
			const data = isJson && await response.json()

			if (!response.ok) {
				const error = (data?.message) || response.status
				return await Promise.reject(new Error(error))
			}

			if (data) {
				return await Promise.resolve(data)
			}
		})
}

export async function fetchText(relativeEndpoint, requestOptions) {
	return await fetch(serverEndpoint + relativeEndpoint, requestOptions)
		.then(async response => {
			const isText = response.headers.get('content-type')?.includes('text/plain')
			const data = isText && await response.text()

			if (!response.ok) {
				const error = data || response.status
				return await Promise.reject(new Error(error))
			}

			if (data) {
				return await Promise.resolve(data)
			}
		})
}

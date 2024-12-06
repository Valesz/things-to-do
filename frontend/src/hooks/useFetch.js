import {useCallback, useEffect, useState} from 'react'

const useFetch = (fetchCallback, enabled) => {
	const [data, setData] = useState(null)
	const [isLoading, setIsLoading] = useState(true)
	const [error, setError] = useState(null)
	const [valid, setValid] = useState(false)

	const fetch = useCallback(async () => {
		if (valid || !enabled) {
			return
		}

		if (!isLoading) {
			setIsLoading(true)
		}

		const response = await fetchCallback()
			.catch(error => setError(error))

		if (response) {
			setData(response)
		}

		setIsLoading(false)
		setValid(true)
	}, [fetchCallback, valid, enabled, isLoading])

	useEffect(() => {
		fetch()
	}, [fetch])

	useEffect(() => {
		setValid(false)
	}, [fetchCallback])

	return [data, error, isLoading, setValid]
}

export default useFetch

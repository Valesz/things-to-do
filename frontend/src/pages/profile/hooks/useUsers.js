import {useCallback} from 'react'
import {fetchUser} from '../sevices/userService'
import useFetch from '../../../hooks/useFetch'

export function useUsers({id, username, email, timeOfCreation, status, classification, precisionOfAnswers, enabled = true, toastRef}) {

	const getUser = useCallback(async () => {
		return await fetchUser({
			id: id,
			username: username,
			email: email,
			timeOfCreation: timeOfCreation,
			status: status,
			classification: classification,
			precisionOfAnswers: precisionOfAnswers
		}).catch(error => {
			toastRef?.current.show({severity: 'error', summary: 'Failed to load users', detail: error.message})
			throw new Error(error)
		})
	}, [id, username, email, timeOfCreation, status, classification, precisionOfAnswers, toastRef])

	const [users, error, isLoading, setValid] = useFetch(getUser, enabled)

	return [users, error, isLoading, setValid]
}
import {useCallback} from 'react'
import {fetchSubmission} from '../services/submissionService'
import useFetch from '../../../hooks/useFetch'

export function useSubmissions({id, taskId, description, timeOfSubmission, acceptance, submitterId, submitterName, pageNumber = 0, pageSize = 5, enabled = true, toastRef}) {

	const getSubmissions = useCallback(async () => {
		return await fetchSubmission({
			id: id,
			taskId: taskId,
			description: description,
			timeOfSubmission: timeOfSubmission,
			acceptance: acceptance,
			submitterId: submitterId,
			submitterName: submitterName,
			pageNumber: pageNumber,
			pageSize: pageSize
		}).catch(error => {
			toastRef?.current.show({severity: 'error', summary: 'Failed to load solutions', detail: error.message})
			throw new Error(error)
		})
	}, [id, taskId, description, timeOfSubmission, acceptance, submitterId, submitterName, pageNumber, pageSize, toastRef])

	const [submissions, error, isLoading, setValid] = useFetch(getSubmissions, enabled)

	return [submissions, error, isLoading, setValid]
}

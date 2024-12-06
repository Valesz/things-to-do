import {useCallback} from 'react'
import {fetchTasks} from '../services/taskService'
import useFetch from '../../../hooks/useFetch'

export function useTasks({id, name, keywords, date, completed, completedUserId, ownerId, creatorName, enabled = true, toastRef}) {

	const getTask = useCallback(async () => {
		return await fetchTasks({
			id: id,
			name: name,
			keywords: keywords,
			date: date,
			completed: completed,
			completedUserId: completedUserId,
			ownerId: ownerId,
			creatorName: creatorName
		}).catch(error => {
			toastRef?.current.show({severity: 'error', summary: 'Failed to load tasks', detail: error.message})
			throw new Error(error)
		})
	}, [id, name, keywords, date, completedUserId, completed, ownerId, creatorName, toastRef])

	const [tasks, error, isLoading, setValid] = useFetch(getTask, enabled)

	return [tasks, error, isLoading, setValid]
}
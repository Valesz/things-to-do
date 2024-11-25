import {useCallback, useState} from 'react'
import {fetchTasks} from '../../../services/taskService'
import TaskFilterVisual from './taskFilterVisual'
import {Button} from 'primereact/button'
import PropTypes from 'prop-types'

const TaskFilterComponent = ({setTasks, toastRef}) => {
	const [filterFormData, setFilterFormData] = useState(
		{
			taskName: {
				label: 'Task name',
				name: 'taskName',
				value: '',
				type: 'text'
			},
			creatorName: {
				label: 'Creator name',
				name: 'creatorName',
				value: '',
				type: 'text'
			},
			keywords: {
				label: 'Keywords',
				name: 'keywords',
				value: [],
				type: 'chips'
			},
			date: {
				label: 'Date',
				name: 'date',
				value: '',
				type: 'date',
				mode: 'range'
			},
			completion: {
				label: 'Completion status:',
				name: 'completion',
				value: '',
				type: 'radio',
				options: [
					{
						label: 'Ignored',
						name: 'ignored',
						value: '',
						selectOnValue: ''
					},
					{
						label: 'Completed',
						name: 'completed',
						value: 'true',
						selectOnValue: 'true'
					},
					{
						label: 'Uncompleted',
						name: 'uncompleted',
						value: 'false',
						selectOnValue: 'false'
					}
				]
			}
		}
	)

	const fetchTasksCallback = useCallback(async () => {
		fetchTasks({
			name: filterFormData.taskName.value,
			keywords: filterFormData.keywords.value,
			date: filterFormData.date.value,
			completed: filterFormData.completion.value,
			creatorName: filterFormData.creatorName.value
		})
			.then((tasks) => {
				setTasks(tasks)
			})
			.catch((error) => {
				toastRef.current.show({severity: 'error', summary: 'Listing error', detail: error})
			})
	}, [filterFormData, setTasks, toastRef])

	return (
		<TaskFilterVisual
			toastRef={toastRef}
			filterFormData={filterFormData}
			setFilterFormData={setFilterFormData}
			submitButton={
				<Button
					label={'Search'}
					type={'submit'}
				/>
			}
			action={fetchTasksCallback}
		/>
	)
}

export default TaskFilterComponent

TaskFilterComponent.propTypes = {
	setTasks: PropTypes.func.isRequired,
	toastRef: PropTypes.object.isRequired
}

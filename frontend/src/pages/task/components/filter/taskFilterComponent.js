import {useCallback, useState} from 'react'
import TaskFilterVisual from './taskFilterVisual'
import {Button} from 'primereact/button'
import PropTypes from 'prop-types'
import {useSearchParams} from 'react-router-dom'

const TaskFilterComponent = ({toastRef, onChange}) => {
	const [searchParams, setSearchParams] = useSearchParams()

	const [filterFormData, setFilterFormData] = useState(
		{
			taskName: {
				label: 'Task name',
				name: 'taskName',
				value: searchParams.get('name') || '',
				type: 'text'
			},
			creatorName: {
				label: 'Creator name',
				name: 'creatorName',
				value: searchParams.get('creatorName') || '',
				type: 'text'
			},
			keywords: {
				label: 'Keywords',
				name: 'keywords',
				value: searchParams.getAll('keywords') || [],
				type: 'chips'
			},
			date: {
				label: 'Date',
				name: 'date',
				value: searchParams.get('date') || '',
				type: 'date',
				mode: 'range'
			},
			completion: {
				label: 'Completion status:',
				name: 'completion',
				value: searchParams.get('completed') || '',
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
		setSearchParams(() => {
			let res = {}
			filterFormData.taskName.value &&
			(res = {
				...res,
				name: filterFormData.taskName.value
			})
			filterFormData.keywords.value &&
			(res = {
				...res,
				keywords: filterFormData.keywords.value
			})
			filterFormData.date.value &&
			(res = {
				...res,
				date: filterFormData.date.value
			})
			filterFormData.completion.value &&
			(res = {
				...res,
				completed: filterFormData.completion.value
			})
			filterFormData.creatorName.value &&
			(res = {
				...res,
				creatorName: filterFormData.creatorName.value
			})
			return res
		})
		onChange?.(filterFormData)
	}, [setSearchParams, filterFormData, onChange])

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
	toastRef: PropTypes.object.isRequired,
	onChange: PropTypes.func
}

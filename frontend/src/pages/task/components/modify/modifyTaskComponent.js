import {useCallback, useEffect, useState} from 'react'
import {useAuth} from '../../../../hooks/useAuth'
import AddTaskVisual from '../add/addTaskVisual'
import {updateTask} from '../../services/taskService'
import PropTypes from 'prop-types'
import {Button} from 'primereact/button'

const ModifyTaskComponent = ({visible, setVisible, toastRef, onSuccess, task}) => {

	const [formData, setFormData] = useState({
		name: {
			id: 'name',
			name: 'name',
			value: task.name,
			type: 'text',
			className: 'h-3rem max-w-20rem md:max-w-full w-full md:w-30rem'
		},
		keywords: {
			id: 'keywords',
			name: 'keywords',
			value: task.keywords,
			type: 'chips',
			className: 'w-full max-w-20rem md:max-w-full',
			containerClassName: 'w-full max-w-20rem md:max-w-full md:w-30rem lg:max-h-15rem overflow-y-auto'
		},
		description: {
			id: 'description',
			name: 'description',
			value: task.description,
			type: 'textArea',
			className: 'w-full max-w-20rem md:max-w-full md:w-30rem'
		}
	})

	const [user, token] = useAuth()

	useEffect(() => {
		setFormData(prevState => {
			return {
				...prevState,
				name: {
					...prevState.name,
					value: task.name
				},
				keywords: {
					...prevState.keywords,
					value: task.keywords
				},
				description: {
					...prevState.description,
					value: task.description
				}
			}
		})
	}, [task])

	const modifyCallback = useCallback(async () => {
		if (!token) {
			return undefined
		}

		//TODO: Ask. Itt vagy backenden legyen. Bár a backend tűnik jobbnak.
		// formData.keywords.value.toString() !== task.keywords.toString()

		await updateTask({
			authToken: token,
			id: task.id,
			name: formData.name.value,
			description: formData.description.value,
			keywords: formData.keywords.value
		}).then(task => {
			task.ownername = user.username
			setVisible(false)
			onSuccess?.(task)
			toastRef.current.show({severity: 'success', summary: 'Task Updated successfully!'})
		}).catch(error => {
			toastRef.current.show({severity: 'error', summary: 'Update Failed', detail: error.message})
		})
	}, [task, token, toastRef, setVisible, formData, user, onSuccess])

	return (
		<AddTaskVisual
			visible={visible}
			setVisible={setVisible}
			toastRef={toastRef}
			addTaskCallback={modifyCallback}
			submitButton={
				<Button label={'Update Task'}/>
			}
			title={'Modify your task!'}
			formData={formData}
			setFormData={setFormData}
		/>
	)
}

export default ModifyTaskComponent

ModifyTaskComponent.propTypes = {
	visible: PropTypes.bool.isRequired,
	setVisible: PropTypes.func.isRequired,
	toastRef: PropTypes.object.isRequired,
	task: PropTypes.object.isRequired,
	onSuccess: PropTypes.func
}

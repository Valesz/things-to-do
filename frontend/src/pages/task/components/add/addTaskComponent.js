import {useCallback, useState} from 'react'
import {addTasks} from '../../services/taskService'
import AddTaskVisual from './addTaskVisual'
import {useAuth} from '../../../../hooks/useAuth'
import PropTypes from 'prop-types'
import {Button} from 'primereact/button'

const AddTaskComponent = ({visible, setVisible, toastRef, onSuccess}) => {

	//TODO: ADD mainTaskId option
	// const [mainTaskId, setMainTaskId] = useState(null);

	const [user, token] = useAuth()

	const [formData, setFormData] = useState({
		name: {
			label: 'Task name',
			name: 'name',
			value: '',
			type: 'text',
			className: 'h-3rem max-w-20rem md:max-w-full w-full md:w-30rem'
		},
		keywords: {
			label: 'Keywords',
			name: 'keywords',
			value: [],
			type: 'chips',
			className: 'w-full max-w-20rem md:max-w-full',
			containerClassName: 'w-full max-w-20rem md:max-w-full md:w-30rem lg:max-h-15rem overflow-y-auto'
		},
		description: {
			label: 'Description',
			name: 'description',
			value: '',
			type: 'textArea',
			className: 'w-full max-w-20rem md:max-w-full md:w-30rem'
		}
	})

	const addTaskCallback = useCallback(async () => {
		await addTasks({
			authToken: token,
			name: formData.name.value,
			description: formData.description.value,
			mainTaskId: null,
			ownerId: user.id,
			timeofcreation: new Date(),
			keywords: formData.keywords.value
		})
			.then((task) => {
				task.ownername = user.username
				setVisible(false)
				onSuccess?.(task)
			}).catch((taskAdditionError) => {
				toastRef.current.show({severity: 'error', summary: 'Task addition error', detail: taskAdditionError.message})
			})
	}, [formData, setVisible, toastRef, token, user, onSuccess])

	return (
		<AddTaskVisual
			visible={visible}
			setVisible={setVisible}
			addTaskCallback={addTaskCallback}
			toastRef={toastRef}
			submitButton={
				<Button label={'Create Task'}/>
			}
			title={'Create your own task!'}
			formData={formData}
			setFormData={setFormData}
		/>
	)
}

export default AddTaskComponent

AddTaskComponent.propTypes = {
	visible: PropTypes.bool.isRequired,
	setVisible: PropTypes.func.isRequired,
	toastRef: PropTypes.object.isRequired,
	onSuccess: PropTypes.func
}

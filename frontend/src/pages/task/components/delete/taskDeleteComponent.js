import {forwardRef, useCallback, useImperativeHandle} from 'react'
import {useAuth} from '../../../../contexts/AuthContext'
import {deleteTask} from '../../services/taskService'
import {confirmWarnDialog} from '../../../../utils/constants/buttons'
import PropTypes from 'prop-types'

const TaskDeleteComponent = forwardRef(({toastRef, onSuccess, setTasks}, ref) => {
	const [, token] = useAuth()

	const deleteTaskCallback = useCallback(async (id) => {
		if (!id) {
			throw new Error('ID not given')
		}

		await deleteTask({authToken: token, id: id})
			.then(() => {
				setTasks?.((prevState) => {
					const index = prevState.findIndex((task) => task.id === id)
					if (index > -1) {
						return prevState.toSpliced(index, 1)
					}
					return prevState
				})
				onSuccess?.(id)
				toastRef.current.show({severity: 'success', summary: `Task deleted successfully!`})
			})
			.catch(error => {
				toastRef.current.show({severity: 'error', summary: 'Deletion failed', detail: error.message})
			})
	}, [token, setTasks, toastRef, onSuccess])

	useImperativeHandle(ref, () => ({
		deleteTaskCallback(id) {
			confirmWarnDialog({
				message: 'Are you sure you would like to delete this task?',
				headerMessage: 'Deletion confirmation',
				classname: 'w-3',
				acceptCallback: () => deleteTaskCallback(id)
			})
		}
	}))
})

export default TaskDeleteComponent

TaskDeleteComponent.propTypes = {
	setTasks: PropTypes.func,
	toastRef: PropTypes.object.isRequired,
	onSuccess: PropTypes.func
}

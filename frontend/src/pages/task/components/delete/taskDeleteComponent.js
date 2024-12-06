import {forwardRef, useCallback, useImperativeHandle} from 'react'
import {useAuth} from '../../../../hooks/useAuth'
import {deleteTask} from '../../services/taskService'
import {confirmWarnDialog} from '../../../../utils/constants/buttons'
import PropTypes from 'prop-types'

const TaskDeleteComponent = forwardRef(({toastRef, onSuccess}, ref) => {
	const [, token] = useAuth()

	const deleteTaskCallback = useCallback(async (id) => {
		if (!id) {
			throw new Error('ID not given')
		}

		await deleteTask({authToken: token, id: id})
			.then(() => {
				onSuccess?.(id)
				toastRef.current.show({severity: 'success', summary: `Task deleted successfully!`})
			})
			.catch(error => {
				toastRef.current.show({severity: 'error', summary: 'Deletion failed', detail: error.message})
			})
	}, [token, toastRef, onSuccess])

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
	toastRef: PropTypes.object.isRequired,
	onSuccess: PropTypes.func
}

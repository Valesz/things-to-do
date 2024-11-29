import {forwardRef, useCallback, useImperativeHandle} from 'react'
import {deleteSubmission} from '../../services/submissionService'
import {useAuth} from '../../../../contexts/AuthContext'
import {confirmWarnDialog} from '../../../../utils/constants/buttons'
import PropTypes from 'prop-types'

const SubmissionDeleteComponent = forwardRef(({toastRef, onSuccess, setSubmissions}, ref) => {
	const [, token] = useAuth()

	const deleteSubmissionCallback = useCallback(async (id) => {
		if (!id) {
			throw new Error('ID not given')
		}

		await deleteSubmission(token, id)
			.then(() => {
				setSubmissions?.(prevState => [...prevState.filter(submission => submission.id !== id)])
				onSuccess?.(id)

				toastRef.current.show({severity: 'success', summary: 'Deletion success', detail: 'Solution deleted successfully!'})
			})
			.catch(error => {
				toastRef.current.show({severity: 'error', summary: 'Failed to delete solution', detail: error.message})
			})
	}, [token, setSubmissions, toastRef, onSuccess])

	useImperativeHandle(ref, () => ({
		deleteSubmissionCallback(id) {
			confirmWarnDialog({
				message: 'Are you sure you would like to delete this submission?',
				headerMessage: 'Deletion confirmation',
				classname: 'w-3',
				acceptCallback: () => deleteSubmissionCallback(id)
			})
		}
	}))
})

export default SubmissionDeleteComponent

SubmissionDeleteComponent.propTypes = {
	toastRef: PropTypes.object.isRequired,
	onSuccess: PropTypes.func,
	setSubmissions: PropTypes.func
}

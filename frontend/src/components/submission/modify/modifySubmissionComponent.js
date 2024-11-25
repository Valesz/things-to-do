import {Dialog} from 'primereact/dialog'
import AddSubmissionVisual from '../add/addSubmissionVisual'
import {useCallback, useEffect, useState} from 'react'
import {Button} from 'primereact/button'
import {modifySubmission} from '../../../services/submissionService'
import {useAuth} from '../../../contexts/AuthContext'
import PropTypes from 'prop-types'

const ModifySubmissionComponent = ({visible, setVisible, toastRef, setSubmissions, submission}) => {

	const [formData, setFormData] = useState({
		description: {
			id: 'description',
			name: 'description',
			label: 'Update your solution',
			value: submission.description,
			type: 'textArea',
			className: 'w-18rem sm:w-30rem h-10rem'
		}
	})
	const [user, token] = useAuth()

	useEffect(() => {
		setFormData(prevState => {
			return {
				...prevState,
				description: {
					...prevState.description,
					value: submission.description
				}
			}
		})
	}, [submission])

	const modifySubmissionCallback = useCallback(async () => {
		await modifySubmission({
			authToken: token,
			id: submission.id,
			taskid: submission.taskid,
			description: formData.description.value,
			acceptance: 'IN_PROGRESS',
			submitterId: submission.submitterid
		})
			.then(submission => {
				submission.submittername = user.username
				setSubmissions(prevState => {
					const index = prevState.findIndex(_submission => _submission.id === submission.id)
					prevState[index] = submission
					return prevState
				})
				setVisible(false)
				toastRef.current.show({severity: 'success', summary: 'Solution updated successfully!'})
			})
			.catch(error => {
				toastRef.current.show({severity: 'error', summary: 'Update failed', detail: error.message})
			})
	}, [formData, token, submission, setSubmissions, setVisible, toastRef, user])

	return (
		<Dialog
			visible={visible}
			onHide={() => setVisible(false)}
			modal
			resizable={false}
			dismissableMask={true}
			header={'Update your solution!'}
		>
			<AddSubmissionVisual
				formData={formData}
				setFormData={setFormData}
				submitButton={
					<Button label={'Update Solution'}/>
				}
				addSubmissionCallback={modifySubmissionCallback}
			/>
		</Dialog>
	)
}

export default ModifySubmissionComponent

ModifySubmissionComponent.propTypes = {
	visible: PropTypes.bool.isRequired,
	setVisible: PropTypes.func.isRequired,
	toastRef: PropTypes.object,
	setSubmissions: PropTypes.func.isRequired,
	submission: PropTypes.object
}

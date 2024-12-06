import {Dialog} from 'primereact/dialog'
import AddSubmissionVisual from '../add/addSubmissionVisual'
import {useCallback, useEffect, useState} from 'react'
import {Button} from 'primereact/button'
import {modifySubmission} from '../../services/submissionService'
import {useAuth} from '../../../../hooks/useAuth'
import PropTypes from 'prop-types'

const ModifySubmissionComponent = ({visible, setVisible, toastRef, submission, onSuccess}) => {

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
				setVisible(false)
				onSuccess?.(submission)
				toastRef.current.show({severity: 'success', summary: 'Solution updated successfully!'})
			})
			.catch(error => {
				toastRef.current.show({severity: 'error', summary: 'Update failed', detail: error.message})
			})
	}, [formData, token, submission, setVisible, toastRef, user, onSuccess])

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
	submission: PropTypes.object,
	onSuccess: PropTypes.func
}

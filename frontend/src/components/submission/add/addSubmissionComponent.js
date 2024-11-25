import AddSubmissionVisual from './addSubmissionVisual'
import {useCallback, useState} from 'react'
import {addSubmission} from '../../../services/submissionService'
import {useAuth} from '../../../contexts/AuthContext'
import {BlockUI} from 'primereact/blockui'
import {Button} from 'primereact/button'
import {Dialog} from 'primereact/dialog'
import LoginComponent from '../../login-register/login/loginComponent'
import PropTypes from 'prop-types'

const AddSubmissionComponent = ({task, setSubmissions, toastRef, onAdd}) => {

	const [loginDialogVisibility, setLoginDialogVisibility] = useState(false)
	const [user, token] = useAuth()

	const [formData, setFormData] = useState({
		description: {
			id: 'description',
			name: 'description',
			label: 'Your Solution',
			value: '',
			type: 'textArea',
			className: 'w-full h-10rem'
		}
	})

	const addSubmissionCallback = useCallback(async () => {
		if (!user) {
			toastRef.current.show({severity: 'error', summary: 'Login required', detail: 'Please login to submit your solution'})
			return
		}

		if (!formData.description.value) {
			toastRef.current.show({severity: 'error', summary: 'Submission failed', detail: 'Submission was empty!'})
			return
		}

		await addSubmission({
			authToken: token,
			taskId: task.id,
			description: formData.description.value,
			timeOfSubmission: new Date(),
			acceptance: 'IN_PROGRESS',
			submitterId: user.id
		})
			.then(submission => {
				setSubmissions(prevState => [submission, ...prevState])
				onAdd?.()
			})
			.catch(error => {
				toastRef.current.show({severity: 'error', summary: 'Failed to submit solution', detail: error})
			})
	}, [formData, token, user, setSubmissions, onAdd, toastRef, task])

	return (
		<>
			<BlockUI
				blocked={!user}
				template={
					<div className={'flex flex-column align-items-center gap-2'}>
						<p className={'text-2xl text-center'}>Please login to submit your solution!</p>
						<Button
							label={'Login'}
							className={'w-10rem'}
							onClick={() => setLoginDialogVisibility(true)}
						/>
					</div>
				}
				containerClassName={'p-3 rounded'}
			>
				<AddSubmissionVisual
					title={`My solution for ${task.name}`}
					formData={formData}
					setFormData={setFormData}
					addSubmissionCallback={addSubmissionCallback}
				/>
			</BlockUI>
			<Dialog
				visible={loginDialogVisibility}
				onHide={() => setLoginDialogVisibility(false)}
				contentClassName={'p-0 h-0'}
				dismissableMask={true}
				headerClassName={'p-0'}
				content={
					<LoginComponent
						className={'w-full min-w-max'} extraButtons={
						[{
							label: 'Close',
							onClick: () => setLoginDialogVisibility(false)
						}]
					}
						toastRef={toastRef}
					/>
				}
			/>
		</>
	)

}

export default AddSubmissionComponent

AddSubmissionComponent.propTypes = {
	task: PropTypes.object.isRequired,
	setSubmissions: PropTypes.func.isRequired,
	toastRef: PropTypes.object.isRequired,
	onAdd: PropTypes.func
}

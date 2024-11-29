import SubmissionBlock from '../list/submissionBlock'
import PropTypes from 'prop-types'
import {useCallback, useMemo} from 'react'
import {modifySubmission} from '../../services/submissionService'
import {submissionAcceptance} from '../../../../utils/constants/submissionEnums'
import {useAuth} from '../../../../contexts/AuthContext'
import {confirmSuccessDialog, confirmWarnDialog} from '../../../../utils/constants/buttons'

const GradeSubmissionComponent = ({submission, enabled, setSubmissions, toastRef}) => {
	const [, token] = useAuth()

	const changeSubmissionAcceptance = useCallback(async (submission, acceptance) => {
		await modifySubmission({authToken: token, ...submission, acceptance: acceptance})
			.then(responseSubmission => {
				responseSubmission.submittername = submission.submittername
				setSubmissions(prevState => {
					const index = prevState.findIndex(_submission => responseSubmission.id === _submission.id)
					if (index >= 0) {
						prevState[index] = responseSubmission
					}
					return [...prevState]
				})
			})
			.catch(error => {
				toastRef.current.show({severity: 'error', detail: 'Grading failed', summary: error.message})
			})
	}, [token, setSubmissions, toastRef])

	const confirmAccept = useCallback(() => confirmSuccessDialog({
		message: 'Are you sure you would like to accept this solution?',
		headerMessage: 'Grading confirmation',
		acceptCallback: () => changeSubmissionAcceptance(submission, submissionAcceptance.ACCEPTED)
	}), [changeSubmissionAcceptance, submission])

	const confirmReject = useCallback(() => confirmWarnDialog({
		message: 'Are you sure you would like to reject this solution?',
		headerMessage: 'Grading confirmation',
		acceptCallback: () => changeSubmissionAcceptance(submission, submissionAcceptance.REJECTED)
	}), [changeSubmissionAcceptance, submission])

	const gradeButtons = useMemo(() => [
		{
			label: 'Accept',
			onClick: confirmAccept
		},
		{
			label: 'Reject',
			severity: 'danger',
			onClick: confirmReject
		}
	], [confirmAccept, confirmReject])

	return (
		<SubmissionBlock
			submission={submission}
			buttons={
				enabled ? gradeButtons : undefined
			}
			titleShow={'submitter'}
		/>
	)
}

export default GradeSubmissionComponent

GradeSubmissionComponent.propTypes = {
	submission: PropTypes.object,
	enabled: PropTypes.bool,
	setSubmissions: PropTypes.func,
	toastRef: PropTypes.object
}

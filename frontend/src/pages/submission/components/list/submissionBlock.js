import {classNames} from 'primereact/utils'
import {Button} from 'primereact/button'
import {useNavigate} from 'react-router-dom'
import PropTypes from 'prop-types'
import {submissionAcceptance} from '../../../../utils/constants/submissionEnums'

const SubmissionBlock = ({task, submission, index, buttons, titleShow = 'both'}) => {

	const navigate = useNavigate()

	if (!submission) {
		return (
			<div className={'flex flex-column justify-content-center align-items-center'}>
				<h2 className={'mb-0'}>Solution not found!</h2>
				<p>Check if you surely clicked on a valid solution.</p>
			</div>
		)
	}

	return (
		<div key={submission.id} className={'col-12 m-1'}>
			<div className={classNames('flex flex-column xl:align-items-start py-2 px-2 gap-3', {'border-top-1 surface-border': index === undefined || index !== 0})}>
				<div className={'grid grid-nogutter w-full'}>
					{
						submission.submitterid &&
						(
							(
								titleShow === 'both' &&
								<span className={'col-7 text-2xl font-bold text-left'}>
									<span>Solution for {task.name}</span>
									<span>
										<span>Solution by:&nbsp;</span>
										<button
											onClick={() => navigate(`/profile/${submission.submitterid}`)}
											className={'underline cursor-pointer m-0 p-0 p-component'}
											style={{border: 'none', background: 'none', fontSize: 'inherit', color: 'inherit', fontWeight: 'inherit'}}
											tabIndex={0}
										>
											<span>
												{submission.submittername}
											</span>
										</button>
										{
											(submission.acceptance === submissionAcceptance.ACCEPTED &&
												<i className={'pi pi-check ml-2 text-xl text-green-400'}></i>) ||
											(submission.acceptance === submissionAcceptance.REJECTED &&
												<i className={'pi pi-times ml-2 text-xl text-red-400'}></i>) ||
											<i className={'pi pi-clock ml-2 text-xl'}></i>
										}
									</span>
								</span>
							)
							|| (
								titleShow === 'submitter' &&
								<span className={'col-7 text-2xl font-bold text-left'}>
									<span>
										<span>Solution by:&nbsp;</span>
										<button
											onClick={() => navigate(`/profile/${submission.submitterid}`)}
											className={'underline cursor-pointer m-0 p-0 p-component'}
											style={{border: 'none', background: 'none', fontSize: 'inherit', color: 'inherit', fontWeight: 'inherit'}}
											tabIndex={0}
										>
											<span>
												{submission.submittername}
											</span>
										</button>
										{
											(submission.acceptance === submissionAcceptance.ACCEPTED &&
												<i className={'pi pi-check ml-2 text-xl text-green-400'}></i>) ||
											(submission.acceptance === submissionAcceptance.REJECTED &&
												<i className={'pi pi-times ml-2 text-xl text-red-400'}></i>) ||
											<i className={'pi pi-clock ml-2 text-xl'}></i>
										}
									</span>
								</span>
							)
							|| (
								titleShow === 'task' &&
								<span className={'col-7 text-2xl font-bold text-left'}>
									<span>Solution for {task.name}</span>
								</span>
							)
						)
					}
					{submission.timeofsubmission && <span className={'col-5 text-400 block text-right'}>{submission.timeofsubmission}</span>}
				</div>
				{
					submission.description &&
					<span className={'w-full overflow-hidden text-overflow-ellipsis'}>
						{submission.description}
					</span>
				}
				{
					buttons &&
					<div className={'flex flex-row gap-2 flex-grow-1 lg:h-3rem mt-1'}>
						{buttons.length > 0 &&
							buttons.map((button) => {
								return (<Button
									key={button.label}
									label={button.label}
									className={button.className}
									severity={button.severity}
									icon={button.icon}
									onClick={() => button.onClick?.(button.param === 'all' ? submission : button.param && submission[button.param])}
								/>)
							})
						}
					</div>
				}
			</div>
		</div>
	)
}

export default SubmissionBlock

SubmissionBlock.propTypes = {
	task: PropTypes.object,
	submission: PropTypes.object,
	index: PropTypes.number,
	buttons: PropTypes.arrayOf(PropTypes.object),
	titleShow: PropTypes.string
}

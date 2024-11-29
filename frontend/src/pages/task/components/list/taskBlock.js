import {classNames} from 'primereact/utils'
import {Tag} from 'primereact/tag'
import {Button} from 'primereact/button'
import {useNavigate} from 'react-router-dom'
import PropTypes from 'prop-types'

const TaskBlock = ({task, index, buttons}) => {

	const navigate = useNavigate()

	if (!task) {
		return null
	}

	return (
		<div key={task.id} className={'col-12 m-1'}>
			<div className={classNames('flex flex-column xl:align-items-start p-4 gap-3', {'border-top-1 surface-border': index === undefined || index !== 0})}>
				<div className={'grid grid-nogutter w-full'}>
					{task.name &&
						<span className={'col-6 text-2xl font-bold text-left'}>
							{task.name}&nbsp;
							<span className={'text-base font-semibold text-400'}>
								<span>- By:&nbsp;</span>
								<button
									onClick={() => navigate(`/profile/${task.ownerid}`)}
									className={'underline cursor-pointer p-component m-0 p-0'}
									style={{background: 'none', border: 'none', fontWeight: 'inherit', fontSize: 'inherit', color: 'inherit'}}
									tabIndex={0}
								>
									{task.ownername}
								</button>
							</span>
						</span>}
					{task.timeofcreation && <span className={'col-6 text-400 block text-right'}>{task.timeofcreation}</span>}
					{task.keywords &&
						<span className={'col-12 flex flex-row flex-wrap gap-1 mt-2'}>
							{task.keywords.map((keyword) => (
								<Tag
									key={keyword}
									className={'px-2 surface-100 text-color-secondary text-sm transition-duration-100'}
									value={keyword}
									rounded
								/>
							))}
						</span>}
				</div>
				{task.description && <span>{task.description}</span>}
				<div className={'flex flex-row gap-2 flex-grow-1 lg:h-3rem mt-3'}>
					{buttons && buttons.length > 0 &&
						buttons.map((button) => {
							return (<Button
								key={button.label}
								label={button.label}
								className={button.className}
								severity={button.severity}
								onClick={() => button.onClick?.(button.param === 'all' ? task : button.param && task[button.param])}
							/>)
						})
					}
				</div>
			</div>
		</div>
	)
}

export default TaskBlock

TaskBlock.propTypes = {
	task: PropTypes.object.isRequired,
	index: PropTypes.number,
	buttons: PropTypes.arrayOf(PropTypes.object)
}

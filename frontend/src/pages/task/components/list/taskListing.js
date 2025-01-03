import TaskBlock from './taskBlock'
import {useCallback, useRef} from 'react'
import PropTypes from 'prop-types'
import {Paginator} from 'primereact/paginator'

const TaskListing = ({taskList, first, rows, totalRows, onPageChange, buttons, className, paginator = true, style, wrapText}) => {
	const topOfList = useRef(null)

	const listTemplate = useCallback((list) => {
		if (!list || list.length === 0) {
			return null
		}

		return (
			<div>
				<div ref={topOfList}></div>
				<div className={'grid grid-nogutter'}>
					{taskList.map((task, index) => (
						<TaskBlock key={task.id} task={task} index={index} buttons={buttons} wrapText={wrapText}/>
					))}
				</div>
			</div>
		)
	}, [taskList, buttons, wrapText])

	return (
		<div className={className} style={style || {height: 'fit-content'}}>
			{(taskList.length > 0
					&&
					<div>
						{
							paginator &&
							<Paginator
								first={first}
								rows={rows}
								totalRecords={totalRows}
								rowsPerPageOptions={[5, 10, 20]}
								onPageChange={(e) => {
									topOfList.current.scrollIntoView({block: 'center'})
									onPageChange?.(e)
								}}
								template={{
									layout: window.innerWidth >= 576
										? 'FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown'
										: 'PrevPageLink PageLinks NextPageLink RowsPerPageDropdown'
								}}
							/>
						}
						{
							listTemplate(taskList)
						}
						{
							paginator &&
							<Paginator
								first={first}
								rows={rows}
								totalRecords={totalRows}
								rowsPerPageOptions={[5, 10, 20]}
								onPageChange={(e) => {
									topOfList.current.scrollIntoView({block: 'center'})
									onPageChange?.(e)
								}}
								template={{
									layout: window.innerWidth >= 576
										? 'FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown'
										: 'PrevPageLink PageLinks NextPageLink RowsPerPageDropdown'
								}}
							/>
						}
					</div>)
				|| <div className={'col-11 lg:col-8 mx-auto z-index-1 surface-50 p-component text-center'}>
					<div className={'flex flex-column justify-content-center h-full p-4 gap-4'}>
						<div>
							<span className={'font-semibold text-2xl text-900'}>No tasks found! 😥</span>
						</div>
						<span>Try again with a different filter.</span>
					</div>
				</div>}
		</div>
	)
}

export default TaskListing

TaskListing.propTypes = {
	taskList: PropTypes.arrayOf(PropTypes.object).isRequired,
	buttons: PropTypes.arrayOf(PropTypes.object),
	className: PropTypes.string,
	first: PropTypes.number.isRequired,
	rows: PropTypes.number.isRequired,
	totalRows: PropTypes.number.isRequired,
	onPageChange: PropTypes.func,
	paginator: PropTypes.bool,
	style: PropTypes.object,
	wrapText: PropTypes.bool,
}

import {DataView} from 'primereact/dataview'
import TaskBlock from './taskBlock'
import {useCallback, useRef, useState} from 'react'
import PropTypes from 'prop-types'

const TaskListing = ({taskList, buttons, className}) => {
	const [first, setFirst] = useState(0)
	const [rows, setRows] = useState(5)

	const topOfList = useRef(null)

	const listTemplate = useCallback((list) => {
		if (!list || list.length === 0) {
			return null
		}

		return (
			<>
				<div ref={topOfList}></div>
				<div className={'grid grid-nogutter'}>
					{taskList.slice(first, first + rows).map((task, index) => (
						<TaskBlock key={task.id} task={task} index={index} buttons={buttons}/>
					))}
				</div>
			</>
		)
	}, [taskList, buttons, first, rows])

	return (
		(taskList.length > 0
			&& <DataView
				value={taskList}
				listTemplate={listTemplate}
				paginator={true}
				first={first}
				rows={rows}
				onPage={(e) => {
					topOfList.current?.scrollIntoView({block: 'center'})
					setFirst(e.first)
					setRows(e.rows)
				}}
				paginatorPosition={'both'}
				pageLinkSize={window.innerWidth >= 576 ? 5 : 3}
				paginatorTemplate={window.innerWidth
				>= 576 ? 'FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown' : 'PrevPageLink PageLinks NextPageLink RowsPerPageDropdown'}
				totalRecords={taskList.length}
				rowsPerPageOptions={[5, 10, 20]}
				className={className || 'col-11 lg:col-8 mx-auto z-index-1'}
			/>)
		|| <div className={'col-11 lg:col-8 mx-auto z-index-1 surface-50 p-component text-center'}>
			<div className={'flex flex-column justify-content-center h-full p-4 gap-4'}>
				<div>
					<span className={'font-semibold text-2xl text-900'}>No tasks found! 😥</span>
				</div>
				<span>Try again with a different filter.</span>
			</div>
		</div>
	)
}

export default TaskListing

TaskListing.propTypes = {
	taskList: PropTypes.arrayOf(PropTypes.object).isRequired,
	buttons: PropTypes.arrayOf(PropTypes.object),
	className: PropTypes.string
}

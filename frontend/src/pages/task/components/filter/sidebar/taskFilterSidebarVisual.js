import {Sidebar} from 'primereact/sidebar'
import TaskFilterComponent from '../taskFilterComponent'
import './taskFilterSidebar.css'
import PropTypes from 'prop-types'

const TaskFilterSidebarComponent = ({visible, onHide, toastRef}) => {

	const content = (
		<div className={'block lg:static sidebarHeight'}>
			<div className="surface-section pb-4 h-full block absolute lg:static left-0 top-0 z-1 border-top-1 surface-border select-none w-full">
				<div className={'flex flex-column h-full px-5'}>
					<div className="flex align-items-center justify-content-between py-4 pt-3 flex-shrink-0">
						<span className={'font-semibold text-2xl text-primary'}>Filter search</span>
					</div>
					<div className="overflow-y-auto w-full w-max">
						<TaskFilterComponent toastRef={toastRef}/>
					</div>
				</div>
			</div>
		</div>
	)

	return (
		<Sidebar
			content={content}
			visible={visible}
			onHide={onHide}
			style={{height: 'fit-content'}}
		/>
	)
}

export default TaskFilterSidebarComponent

TaskFilterSidebarComponent.propTypes = {
	visible: PropTypes.bool.isRequired,
	onHide: PropTypes.func.isRequired,
	toastRef: PropTypes.object.isRequired
}

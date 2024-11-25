import {Button} from 'primereact/button'
import {useCallback, useEffect, useRef, useState} from 'react'
import TaskFilterSidebarComponent from '../components/task/filter/sidebar/taskFilterSidebarVisual'
import {InputText} from 'primereact/inputtext'
import {FloatLabel} from 'primereact/floatlabel'
import {Tooltip} from 'primereact/tooltip'
import {Toast} from 'primereact/toast'
import {Fieldset} from 'primereact/fieldset'
import TaskFilterComponent from '../components/task/filter/taskFilterComponent'
import AddTaskComponent from '../components/task/add/addTaskComponent'
import {fetchTasks} from '../services/taskService'
import TaskListing from '../components/task/list/taskListing'
import {useAuth} from '../contexts/AuthContext'
import {useNavigate} from 'react-router-dom'
import {taskViewButtons} from '../utils/constants/buttons'

const TaskListingPage = () => {
	const [tasks, setTasks] = useState([])
	const toastRef = useRef()

	const [toggleFilter, setToggleFilter] = useState(false)
	const [toggleAdd, setToggleAdd] = useState(false)

	const [filterTaskName, setFilterTaskName] = useState('')

	const [user] = useAuth()
	const navigate = useNavigate()

	const fetchTasksCallback = useCallback(async () => {
		await fetchTasks({name: filterTaskName})
			.then((tasks) => {
				setTasks(tasks)
			})
			.catch((error) => {
				toastRef.current.show({severity: 'error', summary: 'Listing error', detail: error.message})
			})
	}, [filterTaskName])

	useEffect(() => {
		fetchTasksCallback()
	}, [fetchTasksCallback])

	if (tasks === undefined) {

		return (
			<div className={'flex justify-content-center align-items-center h-screen w-full'}>
				<p>Loading...</p>
			</div>
		)
	}

	return (
		<div className={'flex flex-column justify-content-start align-items-center h-screen w-full gap-3 mt-6'}>
			<div className={'flex flex-row gap-3 w-full justify-content-center lg:hidden'}>
				<FloatLabel>
					<InputText onBlur={fetchTasksCallback} className="p-inputtext md:p-inputtext-lg" id="taskName" value={filterTaskName} onChange={(e) => setFilterTaskName(e.target.value)}/>
					<label htmlFor="taskName" className={'p-component'}>Task Name</label>
				</FloatLabel>
				<Button label={'Filter'} outlined className={'hidden md:block'} icon={'pi pi-filter'} onClick={() => setToggleFilter(!toggleFilter)}/>
				<Button outlined className={'block md:hidden'} icon={'pi pi-filter'} onClick={() => setToggleFilter(!toggleFilter)}/>
			</div>

			<div className={'grid grid-nogutter w-full'}>
				<div className={'hidden lg:block'} style={{transform: 'translate(0%, -1.5rem)'}}>
					<Fieldset legend={'Filter'} className={'col-1 xl:ml-3 overflow-y-auto w-20rem'}>
						<TaskFilterComponent setTasks={setTasks} toastRef={toastRef}/>
					</Fieldset>
				</div>
				<TaskListing taskList={tasks} buttons={taskViewButtons(navigate)}/>

			</div>
			{user && (
				<>
					<Button className={'hidden lg:flex fixed p-speeddial p-component h-6rem '} onClick={() => setToggleAdd(true)} tooltip={'Add your own task!'} tooltipOptions={{position: 'left'}}
						icon={'pi pi-plus'} label={'Add Task'} rounded raised style={{bottom: 40, right: 40}}/>
					<Button className={'flex lg:hidden fixed p-speeddial p-component h-5rem w-5rem'} onClick={() => setToggleAdd(true)} icon={'pi pi-plus'} rounded raised
						style={{bottom: 20, right: 20}}/>
				</>
			)}
			<Tooltip target={'.p-speeddial-action'} position={'left'}/>
			<Toast ref={toastRef} position={'top-left'}/>
			{user && <AddTaskComponent visible={toggleAdd} setVisible={setToggleAdd} toastRef={toastRef} setTasks={setTasks}/>}
			<TaskFilterSidebarComponent
				setTasks={setTasks}
				visible={toggleFilter}
				onHide={() => setToggleFilter(false)}
				toastRef={toastRef}
			/>
		</div>
	)
}

export default TaskListingPage
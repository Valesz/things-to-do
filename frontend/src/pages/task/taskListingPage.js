import {Button} from 'primereact/button'
import {useEffect, useMemo, useRef, useState} from 'react'
import TaskFilterSidebarComponent from './components/filter/sidebar/taskFilterSidebarVisual'
import {InputText} from 'primereact/inputtext'
import {FloatLabel} from 'primereact/floatlabel'
import {Tooltip} from 'primereact/tooltip'
import {Toast} from 'primereact/toast'
import {Fieldset} from 'primereact/fieldset'
import TaskFilterComponent from './components/filter/taskFilterComponent'
import AddTaskComponent from './components/add/addTaskComponent'
import TaskListing from './components/list/taskListing'
import {useAuth} from '../../hooks/useAuth'
import {useNavigate, useSearchParams} from 'react-router-dom'
import {taskViewButtons} from '../../utils/constants/buttons'
import {useTasks} from './hooks/useTask'
import {ProgressSpinner} from 'primereact/progressspinner'
import Modal from '../../components/Modal'

const TaskListingPage = () => {
	const toastRef = useRef()
	const [toggleFilter, setToggleFilter] = useState(false)
	const [toggleAdd, setToggleAdd] = useState(false)
	const [first, setFirst] = useState(0)
	const [rows, setRows] = useState(5)
	const [searchParams, setSearchParams] = useSearchParams()
	const params = useMemo(() => {
		return {
			name: searchParams.get('name'),
			keywords: searchParams.getAll('keywords'),
			date: searchParams.getAll('date'),
			completed: searchParams.get('completed') || undefined,
			creatorName: searchParams.get('creatorName')
		}
	}, [searchParams])
	const [tasks, tasksFetchError, isLoading, setTasksInvalid] = useTasks({
		...params,
		pageNumber: first / rows,
		pageSize: rows,
		toastRef: toastRef
	})
	const [user] = useAuth()
	const navigate = useNavigate()

	useEffect(() => {
		if (first >= tasks?.totalTasks) {
			setFirst(0)
		}
	}, [first, tasks])

	const loadingDisplay = useMemo(() => (
		<div className={'flex justify-content-center align-items-center h-screen w-full'}>
			<p className={'w-4'}>Loading...</p>
		</div>
	), [])

	const errorDisplay = useMemo(() => (
		<div className={'flex justify-content-center align-items-center h-screen w-full'}>
			<p className={'w-4'}>{tasksFetchError?.message}</p>
		</div>
	), [tasksFetchError])

	return (
		<div className={'flex flex-column justify-content-start align-items-center w-full gap-3 mt-6'}>
			<Modal show={isLoading}>
				<ProgressSpinner />
			</Modal>
			<div className={'flex flex-row gap-3 w-full justify-content-center lg:hidden'}>
				<FloatLabel>
					<InputText
						onBlur={(e) => {
							e.target.value ?
								searchParams.set('name', e.target.value) :
								searchParams.delete('name')
							setSearchParams([...searchParams.entries()])
						}}
						className="p-inputtext md:p-inputtext-lg"
						id="taskName"
					/>
					<label htmlFor="taskName" className={'p-component'}>Task Name</label>
				</FloatLabel>
				<Button label={'Filter'} outlined className={'hidden md:block'} icon={'pi pi-filter'} onClick={() => setToggleFilter(!toggleFilter)}/>
				<Button outlined className={'block md:hidden'} icon={'pi pi-filter'} onClick={() => setToggleFilter(!toggleFilter)}/>
			</div>

			<div className={'grid grid-nogutter w-full'}>
				<div className={'hidden lg:block'} style={{transform: 'translate(0%, -1.5rem)'}}>
					<Fieldset legend={'Filter'} className={'col-1 xl:ml-3 overflow-y-auto w-20rem'}>
						<TaskFilterComponent
							toastRef={toastRef}
						/>
					</Fieldset>
				</div>
				<div className={'col-11 lg:col-8 mx-auto z-index-1 p-component surface-card'}>
					{
						(tasksFetchError && errorDisplay)
						|| (isLoading && loadingDisplay)
						||
						<TaskListing
							taskList={tasks.tasks}
							buttons={taskViewButtons(navigate)}
							first={first}
							rows={rows}
							totalRows={tasks.totalTasks}
							onPageChange={(e) => {
								setFirst(e.first)
								setRows(e.rows)
							}}
						/>
					}
				</div>
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
			{
				user &&
				<AddTaskComponent
					visible={toggleAdd}
					setVisible={setToggleAdd}
					toastRef={toastRef}
					onSuccess={() => setTasksInvalid(false)}
				/>
			}
			<TaskFilterSidebarComponent

				visible={toggleFilter}
				onHide={() => setToggleFilter(false)}
				toastRef={toastRef}
			/>
		</div>
	)
}

export default TaskListingPage
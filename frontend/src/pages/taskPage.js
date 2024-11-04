import {Button} from 'primereact/button';
import {useCallback, useEffect, useRef, useState} from 'react'
import {DataView} from 'primereact/dataview';
import TaskFilterSidebarComponent from '../components/task/taskFilterSidebar'
import {classNames} from 'primereact/utils'
import {InputText} from 'primereact/inputtext'
import {FloatLabel} from 'primereact/floatlabel'
import {Tooltip} from 'primereact/tooltip'
import {Toast} from 'primereact/toast'
import {Fieldset} from 'primereact/fieldset'
import TaskFilterComponent from '../components/task/taskFilterComponent'
import AddTaskComponent from '../components/task/addTaskComponent'
import {Tag} from 'primereact/tag'
import {fetchTasks} from '../services/taskService'

const TaskPage = () => {
	const [tasks, setTasks] = useState([]);
	const toastRef = useRef();

	const [toggleFilter, setToggleFilter] = useState(false);
	const [toggleAdd, setToggleAdd] = useState(false);

	const [filterTaskName, setFilterTaskName] = useState("");

	const fetchTasksCallback = useCallback(async (name) => {
		await fetchTasks(name)
			.then((tasks) => {
				setTasks(tasks);
			})
			.catch((error) => {
				toastRef.current.show({severity: 'error', detail: "Listing error", description: error})
			})
	}, [])

	useEffect(() => {
		fetchTasksCallback();
	}, [fetchTasksCallback])

	if (tasks === undefined) {

		return (
			<div className={"flex justify-content-center align-items-center h-screen w-full"}>
				<p>Loading...</p>
			</div>
		)
	}

	const taskTemplate = (taskList) => {
		if (taskList.length === 0) {
			return null;
		}

		return (
			<div className={"grid grid-nogutter"}>
				{taskList.map((task, index) => (
					<div key={task.id} className={"col-12 m-1"}>
						<div className={classNames('flex flex-column xl:align-items-start p-4 gap-3', {'border-top-1 surface-border': index !== 0})}>
							<div className={'grid grid-nogutter w-full'}>
								<span className={'col-6 text-2xl font-bold text-left'}>
									{task.name} <span className={'text-base font-semibold text-400'}>- By user ID: {task.ownerid}</span>
								</span>
								<span className={'col-6 text-400 block text-right'}>{task.timeofcreation}</span>
								<span className={'col-12 flex flex-row flex-wrap gap-1 mt-2'}>
									{task.keywords && task.keywords.map((keyword, index) => (
										<Tag key={index} className={'px-2 grow-on-hover surface-100 text-color-secondary text-sm hover:underline transition-duration-100 cursor-pointer'} value={keyword} rounded/>
									))}
								</span>
							</div>
							<span>{task.description}</span>
							<div className={'flex flex-row gap-2 flex-grow-1 lg:h-3rem mt-3'}>
								<Button label={'Submit solution'} className={'bg-green-500 border-green-500'}/>
								<Button label={'View solutions'}/>
							</div>
						</div>
					</div>
				))}
			</div>
		);
	};

	return (
		<div className={'flex flex-column justify-content-start align-items-center h-screen w-full gap-3 mt-6'}>
			<div className={'flex flex-row gap-3 w-full justify-content-center lg:hidden'}>
				<FloatLabel>
					<InputText onBlur={() => fetchTasksCallback(filterTaskName)} className="p-inputtext md:p-inputtext-lg" id="taskName" value={filterTaskName} onChange={(e) => setFilterTaskName(e.target.value)} />
					<label htmlFor="taskName" className={"p-component"}>Task Name</label>
				</FloatLabel>
				<Button label={"Filter"} outlined className={"hidden md:block"} icon={"pi pi-filter"} onClick={() => setToggleFilter(!toggleFilter)} />
				<Button outlined className={"block md:hidden"} icon={"pi pi-filter"} onClick={() => setToggleFilter(!toggleFilter)} />
			</div>

			<div className={"grid grid-nogutter w-full"}>
				<div className={"hidden lg:block"} style={{transform: "translate(0%, -1.5rem)"}}>
					<Fieldset legend={"Filter"} className={"col-1 xl:ml-3 overflow-y-auto w-20rem"}>
						<TaskFilterComponent setTasks={setTasks} toastRef={toastRef} />
					</Fieldset>
				</div>
				{
					(tasks.length > 0
						&& <DataView value={tasks} listTemplate={taskTemplate} layout={"list"} className={"col-11 lg:col-8 mx-auto z-index-1"}/>)
						|| (<div className={"col-11 lg:col-8 mx-auto z-index-1 surface-50 p-component text-center"}>
								<div className={'flex flex-column justify-content-center h-full p-4 gap-4'}>
									<div>
										<span className={'font-semibold text-2xl text-900'}>No tasks found! 😥</span>
									</div>
									<span>Try again with a different filter.</span>
								</div>
							</div>)
				}

			</div>
			<Button className={"hidden lg:flex fixed p-speeddial p-component h-6rem "} onClick={() => setToggleAdd(true)} tooltip={"Add your own task!"} tooltipOptions={{position: 'left'}} icon={"pi pi-plus"} label={"Add Task"} rounded raised style={{bottom: 40, right: 40}} />
			<Button className={"flex lg:hidden fixed p-speeddial p-component h-5rem w-5rem"} onClick={() => setToggleAdd(true)} icon={"pi pi-plus"} rounded raised style={{bottom: 20, right: 20}} />
			<Tooltip target={".p-speeddial-action"} position={"left"} />
			<Toast ref={toastRef} position={'top-left'} />
			<AddTaskComponent visible={toggleAdd} setVisible={setToggleAdd} toastRef={toastRef} setTasks={setTasks} />
			<TaskFilterSidebarComponent setTasks={setTasks} visible={toggleFilter} onHide={() => setToggleFilter(false)}/>
		</div>
	);
}

export default TaskPage;
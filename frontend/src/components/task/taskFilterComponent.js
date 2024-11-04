import {FloatLabel} from 'primereact/floatlabel'
import {InputText} from 'primereact/inputtext'
import {Chips} from 'primereact/chips'
import {Calendar} from 'primereact/calendar'
import {TriStateCheckbox} from 'primereact/tristatecheckbox'
import {Button} from 'primereact/button'
import {useCallback, useState} from 'react'
import {useCookies} from 'react-cookie'
import {fetchTasks} from '../../services/taskService'

const TaskFilterComponent = ({setTasks, toastRef}) => {
	const [filterTaskName, setFilterTaskName] = useState("");
	const [filterCreatorName, setFilterCreatorName] = useState("");
	const [filterKeywords, setFilterKeywords] = useState([]);
	const [filterDate, setFilterDate] = useState(null);
	const [filterCompleted, setFilterCompleted] = useState("");

	const [cookies] = useCookies(['authToken']);

	const fetchTasksCallback = useCallback(async () => {
		fetchTasks(filterTaskName, filterKeywords, filterDate, filterCompleted, filterCreatorName)
			.then((tasks) => {
				setTasks(tasks);
			})
			.catch((error) => {
				toastRef.current.show({severity: 'error', summary: "Listing error", detail: error});
			})
	}, [filterTaskName, filterKeywords, filterDate, filterCompleted, filterCreatorName])

	return (
		<>
			<div className={"flex flex-column gap-5 h-min"}>
				<FloatLabel>
					<InputText className={"w-16rem"} id="taskName" value={filterTaskName} onChange={(e) => setFilterTaskName(e.target.value)} />
					<label htmlFor="taskName">Task Name</label>
				</FloatLabel>
				<FloatLabel>
					<InputText className={"w-16rem"} id="creatorName" value={filterCreatorName} onChange={(e) => setFilterCreatorName(e.target.value)} />
					<label htmlFor="creatorName">Creator Name</label>
				</FloatLabel>
				<FloatLabel>
					<Chips className={"w-16rem"}
						id="keywords"
						value={filterKeywords}
						allowDuplicate={false}
						onAdd={
							(e) =>
								e.value.length < 23
									? setFilterKeywords([...filterKeywords, e.value])
									: toastRef.current.show({severity: 'error', summary: 'Too Long keyword'})
						}
						onRemove={(e) => {
							setFilterKeywords((prevState) => {
								const index = prevState.indexOf(e.value);
								if (index > -1) {
									prevState.splice(index, 1);
								}
								return prevState;
							})
						}}
						pt={{container: {className: "w-16rem lg:max-h-15rem overflow-y-auto"}}}
					/>
					<label htmlFor="keywords">Keywords</label>
				</FloatLabel>
				<FloatLabel>
					<Calendar className={"w-16rem"} id="date" dateFormat={"yy-mm-dd"} value={filterDate} selectionMode={'range'} onChange={(e) => setFilterDate(e.value)} hideOnRangeSelection={true} readOnlyInput={true} touchUI />
					<label htmlFor="date">Date</label>
				</FloatLabel>
				<div className={"grid grid-nogutter"}>
					<TriStateCheckbox inputId="completed" className={"col-1"} disabled={!cookies.authToken} value={filterCompleted} onChange={(e) => setFilterCompleted(cookies.authToken ? e.value : null)} />
					<label htmlFor="completed" className={"col-10 ml-2 w-14rem"}>
						Completion status: <br/>
						{(filterCompleted === null || filterCompleted === ""
								? "Ignored"
								: filterCompleted === true
									? "Completed"
									: "Uncompleted")}
						{!cookies.authToken && " (Login required)"}
					</label>
				</div>
			</div>
			<Button
				label={"Search"}
				className={"w-full mt-4"}
				onClick={fetchTasksCallback}
			/>
		</>
	);
}

export default TaskFilterComponent;
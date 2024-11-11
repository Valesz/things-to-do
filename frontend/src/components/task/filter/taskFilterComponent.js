import {useCallback, useState} from 'react'
import {fetchTasks} from '../../../services/taskService'
import TaskFilterVisual from './taskFilterVisual'
import {useAuth} from '../../../contexts/AuthContext'

const TaskFilterComponent = ({setTasks, toastRef}) => {
	const [filterTaskName, setFilterTaskName] = useState("");
	const [filterCreatorName, setFilterCreatorName] = useState("");
	const [filterKeywords, setFilterKeywords] = useState([]);
	const [filterDate, setFilterDate] = useState(null);
	const [filterCompleted, setFilterCompleted] = useState("");

	const [user] = useAuth();

	const fetchTasksCallback = useCallback(async () => {
		fetchTasks({
			name: filterTaskName,
			keywords: filterKeywords,
			date: filterDate,
			completed: filterCompleted,
			creatorName: filterCreatorName
		})
			.then((tasks) => {
				setTasks(tasks);
			})
			.catch((error) => {
				toastRef.current.show({severity: 'error', summary: "Listing error", detail: error});
			})
	}, [filterTaskName, filterKeywords, filterDate, filterCompleted, filterCreatorName, toastRef, setTasks])

	return (
		<TaskFilterVisual
			name={filterTaskName}
			setName={setFilterTaskName}
			creatorName={filterCreatorName}
			setCreatorName={setFilterCreatorName}
			keywords={filterKeywords}
			setKeywords={setFilterKeywords}
			date={filterDate}
			setDate={setFilterDate}
			completed={filterCompleted}
			setCompleted={setFilterCompleted}
			fetchTasksCallback={fetchTasksCallback}
			disabled={!user}
			toastRef={toastRef}
		/>
	);
}

export default TaskFilterComponent;
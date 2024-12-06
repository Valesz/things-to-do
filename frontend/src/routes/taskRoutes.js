import {Route, Routes} from 'react-router-dom'
import TaskListingPage from '../pages/task/taskListingPage'
import TaskPage from '../pages/task/taskPage'
import NotFoundPage from '../pages/error/notFoundPage'

const TaskRoutes = () => {
	return (
		<Routes>
			<Route index element={<TaskListingPage/>}/>
			<Route path={':taskId'} element={<TaskPage/>}>
				<Route path={':activeTab'} element={<TaskPage/>}>
					<Route path={':solutionId'} element={<TaskPage/>}/>
				</Route>
			</Route>
			<Route path={'*'} element={<NotFoundPage/>}/>
		</Routes>
	)
}

export default TaskRoutes
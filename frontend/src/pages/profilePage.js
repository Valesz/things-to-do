import {useNavigate, useParams} from 'react-router-dom'

import {useCallback, useEffect, useRef, useState} from 'react'
import {fetchUser} from '../services/userService'
import {useAuth} from '../contexts/AuthContext'
import ProfileBlock from '../components/profile/profileBlock'
import {Toast} from 'primereact/toast'
import TaskListing from '../components/task/list/taskListing'
import {deleteTask, fetchTasks} from '../services/taskService'
import {TabPanel, TabView} from 'primereact/tabview'
import {ConfirmDialog} from 'primereact/confirmdialog'
import ModifyTaskComponent from '../components/task/modify/modifyTaskComponent'
import {confirmDeleteDialog, openDialog, taskViewButtons, updateDeleteButtons, viewSolutionsForTaskButtons} from '../utils/constants/buttons'

function ProfilePage() {
	const [user, token] = useAuth()
	const [localUser, setLocalUser] = useState(null)
	const {id} = useParams()
	const toastRef = useRef(null)
	const [createdTasks, setCreatedTasks] = useState([])
	// const [submissions, setSubmissions] = useState([]);
	const [completedTasks, setCompletedTasks] = useState([])
	const [modifyVisible, setModifyVisible] = useState(false)
	const [modifyTask, setModifyTask] = useState({
		name: '',
		keywords: [],
		description: ''
	})
	const navigate = useNavigate()

	const getUserCallback = useCallback(async () => {
		if (id === undefined) {
			setLocalUser(user)
			return
		}

		await fetchUser({id: id})
			.then(userData => {
				setLocalUser(userData[0])
			})
			.catch(error => {
				toastRef.current.show({severity: 'error', summary: 'Couldn\'t load user', detail: error})
			})

	}, [id, user])

	useEffect(() => {
		getUserCallback()
	}, [getUserCallback])

	const getTasksOfUser = useCallback(async () => {
		if (!localUser) {
			return
		}

		await fetchTasks({ownerId: localUser.id})
			.then((tasks) => {
				setCreatedTasks(tasks)
			})
			.catch(error => {
				toastRef.current.show({severity: 'error', summary: 'Failed to get tasks', detail: error})
			})
	}, [localUser])

	useEffect(() => {
		getTasksOfUser()
	}, [getTasksOfUser])

	// const getSubmissionsForTaskByUser = useCallback(async () => {
	// 	if (!localUser) {
	// 		return;
	// 	}
	//
	// 	await fetchSubmission({submitterId: localUser.id})
	// 		.then(submissions => {
	// 			setSubmissions(submissions)
	// 		})
	// 		.catch(error => {
	// 			toastRef.current.show({severity: 'error', summary: 'Failed to get submissions', detail: error});
	// 		})
	// }, [localUser])

	const getCompletedTasksOfUser = useCallback(async () => {
		if (!localUser) {
			return
		}

		await fetchTasks({
			completed: true,
			completedUserId: localUser.id
		})
			.then(tasks => {
				setCompletedTasks(tasks)
			})
			.catch(error => {
				toastRef.current.show({severity: 'error', summary: 'Failed to get completed tasks', detail: error})
			})
	}, [localUser])

	useEffect(() => {
		getCompletedTasksOfUser()
	}, [getCompletedTasksOfUser])

	const deleteTaskCallback = useCallback(async (id) => {
		if (!id) {
			return
		}

		await deleteTask({authToken: token, id: id})
			.then(() => {
				setCreatedTasks((prevState) => {
					const index = prevState.findIndex((task) => task.id === id)
					if (index > -1) {
						return prevState.toSpliced(index, 1)
					}
					return prevState
				})
				toastRef.current.show({severity: 'success', summary: `Task deleted successfully!`})
			})
			.catch(error => {
				toastRef.current.show({severity: 'error', summary: 'Deletion failed', detail: error})
			})
	}, [token])

	return (
		<div>
			<ProfileBlock
				user={localUser}
				title={`${localUser ? localUser.username : 'Unknown'}'s Profile`}
				buttons={
					localUser === user ?
						updateDeleteButtons('Modify Profile', 'Delete Profile') :
						undefined
				}
			/>
			{localUser &&
				<TabView className={'px-2 lg:px-8'} pt={{panelContainer: {className: 'p-0'}}}>
					<TabPanel header={'Completed tasks'} className={'w-full sm:w-auto'}>
						<TaskListing
							taskList={completedTasks}
							buttons={viewSolutionsForTaskButtons(navigate)}
							className={'col-12 mx-auto'}
						/>
					</TabPanel>
					<TabPanel header={'Tasks created'} className={'w-full sm:w-auto'}>
						<TaskListing
							taskList={createdTasks}
							buttons={
								(user && user.id === localUser.id
									&& updateDeleteButtons('Modify Task', 'Delete Task',
										(task) => openDialog(setModifyVisible, () => setModifyTask(task)),
										(id) => confirmDeleteDialog(id, 'Are you sure you want to delete this task?', deleteTaskCallback)))
								|| taskViewButtons(navigate)
							}
							className={'col-12 mx-auto'}
						/>
					</TabPanel>
				</TabView>
			}
			<ConfirmDialog dismissableMask={true}/>
			<ModifyTaskComponent
				visible={modifyVisible}
				setVisible={setModifyVisible}
				toastRef={toastRef}
				setTasks={setCreatedTasks}
				task={modifyTask}
			/>
			<Toast ref={toastRef}/>
		</div>
	)
}

export default ProfilePage

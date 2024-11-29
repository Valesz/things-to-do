import {useNavigate, useParams} from 'react-router-dom'

import {useCallback, useEffect, useRef, useState} from 'react'
import {fetchUser} from './sevices/userService'
import {useAuth} from '../../contexts/AuthContext'
import ProfileBlock from './components/profileBlock'
import {Toast} from 'primereact/toast'
import TaskListing from '../task/components/list/taskListing'
import {fetchTasks} from '../task/services/taskService'
import {TabPanel, TabView} from 'primereact/tabview'
import {ConfirmDialog} from 'primereact/confirmdialog'
import ModifyTaskComponent from '../task/components/modify/modifyTaskComponent'
import {openDialog, taskViewButtons, updateDeleteButtons, viewSolutionsForTaskButtons} from '../../utils/constants/buttons'
import ProfileModifyComponent from './components/modify/profileModifyComponent'
import {Dialog} from 'primereact/dialog'
import ProfileDeleteComponent from './components/delete/profileDeleteComponent'
import TaskDeleteComponent from '../task/components/delete/taskDeleteComponent'

function ProfilePage() {
	const [user] = useAuth()
	const [localUser, setLocalUser] = useState(null)
	const {id} = useParams()
	const toastRef = useRef(null)
	const profileDeleteRef = useRef(null)
	const taskDeleteRef = useRef(null)
	const [createdTasks, setCreatedTasks] = useState([])
	// const [submissions, setSubmissions] = useState([]);
	const [completedTasks, setCompletedTasks] = useState([])
	const [modifyProfileVisible, setModifyProfileVisible] = useState(false)
	const [modifyTaskVisible, setModifyTaskVisible] = useState(false)
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
				toastRef.current.show({severity: 'error', summary: 'Couldn\'t load user', detail: error.message})
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
				toastRef.current.show({severity: 'error', summary: 'Failed to get tasks', detail: error.message})
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
				toastRef.current.show({severity: 'error', summary: 'Failed to get completed tasks', detail: error.message})
			})
	}, [localUser])

	useEffect(() => {
		getCompletedTasksOfUser()
	}, [getCompletedTasksOfUser])

	return (
		<div>
			<ProfileBlock
				user={localUser}
				title={`${localUser ? localUser.username : 'Unknown'}'s Profile`}
				buttons={
					localUser?.id === user?.id ?
						updateDeleteButtons('Modify Profile', 'Delete Profile',
							() => setModifyProfileVisible(true),
							(id) => profileDeleteRef.current.deleteProfile(id)) :
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
										(task) => openDialog(setModifyTaskVisible, () => setModifyTask(task)),
										(id) => taskDeleteRef.current.deleteTaskCallback(id)))
								|| taskViewButtons(navigate)
							}
							className={'col-12 mx-auto'}
						/>
					</TabPanel>
				</TabView>
			}
			<ConfirmDialog dismissableMask={true}/>
			<ModifyTaskComponent
				visible={modifyTaskVisible}
				setVisible={setModifyTaskVisible}
				toastRef={toastRef}
				setTasks={setCreatedTasks}
				task={modifyTask}
			/>
			<Dialog
				visible={modifyProfileVisible}
				onHide={() => setModifyProfileVisible(false)} dismissableMask={true}
				className={'w-10 sm:w-8 md:w-6 lg:w-4 xl:w-3'}
				content={
					user &&
					<ProfileModifyComponent
						user={user}
						toastRef={toastRef}
						className={'w-full'}
						extraButtons={[{
							label: 'Close',
							onClick: () => setModifyProfileVisible(false)
						}]}
					/>
				}
			>
			</Dialog>
			<ProfileDeleteComponent ref={profileDeleteRef} toastRef={toastRef} navigationUrl={'/login'}/>
			<TaskDeleteComponent ref={taskDeleteRef} toastRef={toastRef} setTasks={setCreatedTasks}/>
			<Toast ref={toastRef}/>
		</div>
	)
}

export default ProfilePage

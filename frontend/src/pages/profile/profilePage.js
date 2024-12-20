import {useNavigate, useParams} from 'react-router-dom'

import {useCallback, useMemo, useRef, useState} from 'react'
import {useAuth} from '../../hooks/useAuth'
import ProfileBlock from './components/profileBlock'
import {Toast} from 'primereact/toast'
import TaskListing from '../task/components/list/taskListing'
import {TabPanel, TabView} from 'primereact/tabview'
import {ConfirmDialog} from 'primereact/confirmdialog'
import ModifyTaskComponent from '../task/components/modify/modifyTaskComponent'
import {openDialog, taskViewButtons, updateDeleteButtons, viewSolutionForTask, viewSolutionsForTaskButtons} from '../../utils/constants/buttons'
import ProfileModifyComponent from './components/modify/profileModifyComponent'
import {Dialog} from 'primereact/dialog'
import ProfileDeleteComponent from './components/delete/profileDeleteComponent'
import TaskDeleteComponent from '../task/components/delete/taskDeleteComponent'
import {useUsers} from './hooks/useUsers'
import {useTasks} from '../task/hooks/useTask'
import {useSubmissions} from '../submission/hooks/useSubmissions'
import SubmissionListing from '../submission/components/list/submissionListing'
import NotFoundPage from '../error/notFoundPage'

function ProfilePage() {
	const [user] = useAuth()
	const {id} = useParams()
	const toastRef = useRef(null)
	const profileDeleteRef = useRef(null)
	const taskDeleteRef = useRef(null)
	const [localUsers, localUserFetchError, isLocalUserLoading, setLocalUserValid] = useUsers({
		id: id ?? user?.id,
		enabled: !!id || !!user?.id,
		pageNumber: 0,
		pageSize: 1,
		toastRef: toastRef
	})
	const localUser = useMemo(() => localUsers?.users?.[0], [localUsers])
	const [firstCreatedTasks, setFirstCreatedTasks] = useState(0)
	const [rowsCreatedTasks, setRowsCreatedTasks] = useState(5)
	const [createdTasks, createdTaskError, isCreatedTasksLoading, setCreatedTasksValid] = useTasks({
		creatorName: localUser?.username,
		enabled: !!localUser?.username,
		pageNumber: firstCreatedTasks / rowsCreatedTasks,
		pageSize: rowsCreatedTasks,
		toastRef: toastRef
	})
	const [firstCompletedTasks, setFirstCompletedTasks] = useState(0)
	const [rowsCompletedTasks, setRowsCompletedTasks] = useState(5)
	const [completedTasks, completedTaskError, isCompletedTasksLoading] = useTasks({
		completed: true,
		completedUserId: localUser?.id,
		pageNumber: firstCompletedTasks / rowsCompletedTasks,
		pageSize: rowsCompletedTasks,
		enabled: !!localUser?.id && !!createdTasks,
		toastRef: toastRef
	})
	const [firstSubmissions, setFirstSubmissions] = useState(0)
	const [rowsSubmissions, setRowsSubmissions] = useState(5)
	const [submissions, submissionError, isSubmissionLoading] = useSubmissions({
		submitterId: localUser?.id,
		pageNumber: firstSubmissions / rowsSubmissions,
		pageSize: rowsSubmissions,
		enabled: !!localUser?.id,
		toastRef: toastRef
	})
	const [modifyProfileVisible, setModifyProfileVisible] = useState(false)
	const [modifyTaskVisible, setModifyTaskVisible] = useState(false)
	const [modifyTask, setModifyTask] = useState({
		name: '',
		keywords: [],
		description: ''
	})
	const navigate = useNavigate()

	const loadingDisplay = useMemo(() => (
		<div className={'flex justify-content-center align-items-center h-screen w-full'}>
			<p>Loading...</p>
		</div>
	), [])

	const errorDisplay = useCallback((error) => (
		<div className={'flex justify-content-center align-items-center h-screen w-full'}>
			<p>{error.message}</p>
		</div>
	), [])

	if (id && !parseInt(id)) {
		return (
			<NotFoundPage/>
		)
	}

	return (
		<div>
			{(localUserFetchError && errorDisplay(localUserFetchError))
				|| (isLocalUserLoading && loadingDisplay)
				||
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
				/>}
			{localUser &&
				<TabView className={'px-2 lg:px-8'} pt={{panelContainer: {className: 'p-0'}}}>
					<TabPanel header={'Completed tasks'} className={'w-full sm:w-auto'}>
						{
							(completedTaskError && errorDisplay(completedTaskError))
							|| (isCompletedTasksLoading && loadingDisplay)
							||
							<TaskListing
								taskList={completedTasks?.tasks}
								buttons={viewSolutionsForTaskButtons(navigate)}
								totalRows={completedTasks?.totalTasks}
								first={firstCompletedTasks}
								rows={rowsCompletedTasks}
								onPageChange={(e) => {
									setFirstCompletedTasks(e.first)
									setRowsCompletedTasks(e.rows)
								}}
							/>
						}
					</TabPanel>
					<TabPanel header={'Tasks created'} className={'w-full sm:w-auto'}>
						{
							(createdTaskError && errorDisplay(createdTaskError))
							|| (isCreatedTasksLoading && loadingDisplay)
							||
							<TaskListing
								taskList={createdTasks?.tasks}
								buttons={
									(user && user.id === localUser.id
										&& updateDeleteButtons('Modify Task', 'Delete Task',
											(task) => openDialog(setModifyTaskVisible, () => setModifyTask(task)),
											(id) => taskDeleteRef.current.deleteTaskCallback(id)))
									|| taskViewButtons(navigate)
								}
								totalRows={createdTasks?.totalTasks}
								first={firstCreatedTasks}
								rows={rowsCreatedTasks}
								onPageChange={(e) => {
									setFirstCreatedTasks(e.first)
									setRowsCreatedTasks(e.rows)
								}}
							/>
						}
					</TabPanel>
					<TabPanel header={'Submissions'} className={'w-full sm:w-auto'}>
						{
							(submissionError && errorDisplay(submissionError))
							|| (isSubmissionLoading && loadingDisplay)
							||
							<SubmissionListing
								submissionList={submissions.submissions}
								titleShow={'both'}
								buttons={viewSolutionForTask(navigate)}
								first={firstSubmissions}
								rows={rowsSubmissions}
								totalRows={submissions.totalRows}
								onPageChange={(e) => {
									setFirstSubmissions(e.first)
									setRowsSubmissions(e.rows)
								}}
							/>
						}
					</TabPanel>
				</TabView>
			}
			<ConfirmDialog dismissableMask={true}/>
			<ModifyTaskComponent
				visible={modifyTaskVisible}
				setVisible={setModifyTaskVisible}
				toastRef={toastRef}
				onSuccess={() => setCreatedTasksValid(false)}
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
						onSuccess={() => setLocalUserValid(false)}
					/>
				}
			>
			</Dialog>
			<ProfileDeleteComponent
				ref={profileDeleteRef}
				toastRef={toastRef}
				navigationUrl={'/login'}
				onSuccess={() => setLocalUserValid(false)}
			/>
			<TaskDeleteComponent
				ref={taskDeleteRef}
				toastRef={toastRef}
				onSuccess={() => setCreatedTasksValid(false)}
			/>
			<Toast ref={toastRef}/>
		</div>
	)
}

export default ProfilePage

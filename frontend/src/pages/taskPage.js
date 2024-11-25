import {useNavigate, useParams} from 'react-router-dom'
import {useCallback, useEffect, useMemo, useRef, useState} from 'react'
import {fetchTasks} from '../services/taskService'
import TaskBlock from '../components/task/list/taskBlock'
import {Divider} from 'primereact/divider'
import SubmissionListing from '../components/submission/list/submissionListing'
import {deleteSubmission, fetchSubmission} from '../services/submissionService'
import {Toast} from 'primereact/toast'
import {TabPanel} from 'primereact/tabview'
import AddSubmissionComponent from '../components/submission/add/addSubmissionComponent'
import SubmissionBlock from '../components/submission/list/submissionBlock'
import DynamicTabView from '../components/dynamicTabView'
import {confirmDeleteDialog, openDialog, updateDeleteButtons} from '../utils/constants/buttons'
import {useAuth} from '../contexts/AuthContext'
import {ConfirmDialog} from 'primereact/confirmdialog'
import ModifySubmissionComponent from '../components/submission/modify/modifySubmissionComponent'

const TaskPage = () => {
	const [task, setTask] = useState({})
	const [submissions, setSubmissions] = useState([])
	const {taskId, solutionId, activeTab} = useParams()
	const [activeTabIndex, setActiveTabIndex] = useState(0)
	const [extraSubmissionIds, setExtraSubmissionIds] = useState(solutionId ? [parseInt(solutionId)] : [])
	const [submissionModifyVisible, setSubmissionModifyVisible] = useState(false)
	const [modifySubmission, setModifySubmission] = useState({description: ''})

	const navigate = useNavigate()

	const toastRef = useRef()

	const [user, token] = useAuth()

	useEffect(() => {
		!solutionId && setActiveTabIndex(() => {
			switch (activeTab) {
				case 'submit':
					return 0
				case 'solutions':
					return 1
				default:
					return 0
			}
		})

		solutionId && setActiveTabIndex(2)

		//eslint-disable-next-line
	}, [activeTab])

	const getTask = useCallback(async () => {
		await fetchTasks({id: taskId})
			.then(task => {
				setTask(task[0])
			})
			.catch(error => {
				toastRef.current.show({severity: 'error', summary: 'Failed to get task', detail: error})
			})
	}, [taskId])

	useEffect(() => {
		getTask()
	}, [getTask])

	const getSubmissions = useCallback(async () => {
		await fetchSubmission({taskId: taskId})
			.then(submissions => {
				setSubmissions(submissions)
			})
			.catch(error => {
				toastRef.current.show({severity: 'error', summary: 'Failed to get submissions', detail: error})
			})
	}, [taskId])

	useEffect(() => {
		getSubmissions()
	}, [getSubmissions])

	const addExtraTabId = useCallback((id) => {
		if (!extraSubmissionIds.some(_id => _id === id)) {
			setExtraSubmissionIds(prevState => [...prevState, id])
			setActiveTabIndex(extraSubmissionIds.length + 2)
		} else {
			setActiveTabIndex(extraSubmissionIds.indexOf(id) + 2)
		}
	}, [extraSubmissionIds])

	const submissionButtons = useMemo(() => [
		{
			label: 'View',
			onClick: (id) => {
				addExtraTabId(id)
				navigate(`/task/${taskId}/solutions/${id}`)
			},
			param: 'id'
		}
	], [navigate, taskId, addExtraTabId])

	const deleteSubmissionCallback = useCallback(async (id) => {
		if (!id) {
			return
		}

		await deleteSubmission(token, id)
			.then(() => {
				setSubmissions(prevState => [...prevState.filter(submission => submission.id !== id)])
				setExtraSubmissionIds(prevState => [...prevState.filter(_id => _id !== id)])
				setActiveTabIndex(prevState => prevState - 1)
				toastRef.current.show({severity: 'success', detail: 'Solution deleted successfully!'})
			})
			.catch(error => {
				toastRef.current.show({severity: 'error', summary: 'Failed to delete solution', detail: error.message})
			})
	}, [token])

	const baseTabs = useMemo(() => [
		<TabPanel header={'Submit solutions'} key={-1}>
			<AddSubmissionComponent
				task={task}
				setSubmissions={setSubmissions}
				toastRef={toastRef}
				onAdd={() => setActiveTabIndex(1)}
			/>
		</TabPanel>,
		<TabPanel header={'View solutions'} key={-2}>
			<SubmissionListing
				submissionList={submissions}
				titleShow={'submitter'}
				buttons={submissionButtons}
			/>
		</TabPanel>
	], [submissions, task, submissionButtons])

	return (
		<div className={'p-component w-full px-3 lg:px-6 mt-4'} style={{height: 'fit-content', maxHeight: '100vh'}}>
			<div className={'w-full grid grid-nogutter surface-card h-full'}>
				<div className={'col-12 lg:col-6'}>
					<TaskBlock task={task} index={0}/>
				</div>
				<Divider layout={'vertical'} className={'hidden lg:block p-0 m-0'}/>
				<Divider layout={'horizontal'} className={'block lg:hidden p-0 mt-0'}/>
				<div className={'col-12 lg:col-6 px-0 lg:px-4'}>
					{
						submissions &&
						<DynamicTabView
							activeTabIndex={activeTabIndex}
							setActiveTabIndex={setActiveTabIndex}
							tabTemplate={(id) => (
								<TabPanel header={`Solution ${id}`} key={id} closable={true}>
									<SubmissionBlock
										submission={submissions.find(submission => submission.id === parseInt(id))}
										buttons={
											user && submissions.find(submissions => submissions.id === parseInt(id))?.submitterid === user.id ?
												updateDeleteButtons('Modify Solution', 'Delete submission',
													(submission) => openDialog(setSubmissionModifyVisible, () => setModifySubmission(submission)),
													(id) => confirmDeleteDialog(id, 'Are you sure you want to delete this solution?', deleteSubmissionCallback))
												: undefined
										}
										titleShow={'submitter'}
									/>
								</TabPanel>
							)}
							baseTabs={baseTabs}
							extraTabValues={extraSubmissionIds}
							onTabClose={(e) => {
								setExtraSubmissionIds(prevState => [...prevState.filter((id, index) => index !== e.index - baseTabs.length)])
							}}
						/>
					}
				</div>
			</div>
			<ConfirmDialog dismissableMask={true}/>
			<ModifySubmissionComponent
				setVisible={setSubmissionModifyVisible}
				visible={submissionModifyVisible}
				setSubmissions={setSubmissions}
				toastRef={toastRef}
				submission={modifySubmission}
			/>
			<Toast ref={toastRef}/>
		</div>
	)
}

export default TaskPage

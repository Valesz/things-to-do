import {useNavigate, useParams} from 'react-router-dom'
import {useCallback, useEffect, useMemo, useRef, useState} from 'react'
import {fetchTasks} from './services/taskService'
import TaskBlock from './components/list/taskBlock'
import {Divider} from 'primereact/divider'
import SubmissionListing from '../submission/components/list/submissionListing'
import {fetchSubmission} from '../submission/services/submissionService'
import {Toast} from 'primereact/toast'
import {TabPanel} from 'primereact/tabview'
import AddSubmissionComponent from '../submission/components/add/addSubmissionComponent'
import SubmissionBlock from '../submission/components/list/submissionBlock'
import DynamicTabView from '../../components/dynamicTabView'
import {openDialog, updateDeleteButtons} from '../../utils/constants/buttons'
import {useAuth} from '../../contexts/AuthContext'
import {ConfirmDialog} from 'primereact/confirmdialog'
import ModifySubmissionComponent from '../submission/components/modify/modifySubmissionComponent'
import GradeSubmissionComponent from '../submission/components/grade/gradeSubmissionComponent'
import SubmissionDeleteComponent from '../submission/components/delete/submissionDeleteComponent'

const TaskPage = () => {
	const [task, setTask] = useState({})
	const [submissions, setSubmissions] = useState([])
	const {taskId, solutionId, activeTab} = useParams()
	const [activeTabIndex, setActiveTabIndex] = useState(0)
	const [extraSubmissionIds, setExtraSubmissionIds] = useState(solutionId ? [parseInt(solutionId)] : [])
	const [submissionModifyDialogVisible, setSubmissionModifyDialogVisible] = useState(false)
	const [modifySubmissionObject, setModifySubmissionObject] = useState({description: ''})
	const [gradingMode, setGradingMode] = useState(false)

	const navigate = useNavigate()

	const toastRef = useRef()
	const deleteSubmissionRef = useRef()

	const [user] = useAuth()

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
			label: !gradingMode ? 'View' : 'Grade',
			onClick: (id) => {
				addExtraTabId(id)
				navigate(`/task/${taskId}/solutions/${id}`)
			},
			param: 'id',
			severity: !gradingMode ? 'normal' : 'success'
		}
	], [navigate, taskId, addExtraTabId, gradingMode])

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

	const submissionTabTemplate = useCallback((id) => (
		<TabPanel header={`Solution ${id}`} key={id} closable={true}>
			{
				gradingMode ?
					<GradeSubmissionComponent
						submission={submissions.find(submission => submission.id === parseInt(id))}
						enabled={gradingMode}
						setSubmissions={setSubmissions}
						toastRef={toastRef}
					/> :
					<SubmissionBlock
						submission={submissions.find(submission => submission.id === parseInt(id))}
						buttons={
							user && submissions.find(submissions => submissions.id === parseInt(id))?.submitterid === user.id ?
								updateDeleteButtons('Modify Solution', 'Delete submission',
									(submission) => openDialog(setSubmissionModifyDialogVisible, () => setModifySubmissionObject(submission)),
									(id) => deleteSubmissionRef.current.deleteSubmissionCallback(id))
								: undefined
						}
						titleShow={'submitter'}
					/>
			}
		</TabPanel>
	), [submissions, user, gradingMode])

	const gradingButton = useMemo(() => {
		return [{
			label: gradingMode ? 'Finish grading' : 'Grade solutions!',
			onClick: () => setGradingMode(prevState => !prevState),
			severity: gradingMode ? 'warning' : 'normal'
		}]
	}, [gradingMode])

	return (
		<div className={'p-component w-full px-3 lg:px-6 mt-4'} style={{height: 'fit-content', maxHeight: '100vh'}}>
			<div className={'w-full grid grid-nogutter surface-card h-full'}>
				<div className={'col-12 lg:col-6'}>
					<TaskBlock task={task} index={0} buttons={user?.id === task?.ownerid ? gradingButton : undefined}/>
				</div>
				<Divider layout={'vertical'} className={'hidden lg:block p-0 m-0'}/>
				<Divider layout={'horizontal'} className={'block lg:hidden p-0 mt-0'}/>
				<div className={'col-12 lg:col-6 px-0 lg:px-4'}>
					{
						submissions &&
						<DynamicTabView
							activeTabIndex={activeTabIndex}
							setActiveTabIndex={setActiveTabIndex}
							tabTemplate={submissionTabTemplate}
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
				setVisible={setSubmissionModifyDialogVisible}
				visible={submissionModifyDialogVisible}
				setSubmissions={setSubmissions}
				toastRef={toastRef}
				submission={modifySubmissionObject}
			/>
			<SubmissionDeleteComponent
				ref={deleteSubmissionRef}
				setSubmissions={setSubmissions}
				toastRef={toastRef}
				onSuccess={(id) => {
					setExtraSubmissionIds(prevState => [...prevState.filter(_id => _id !== id)])
					setActiveTabIndex(prevState => prevState - 1)
				}}
			/>
			<Toast ref={toastRef}/>
		</div>
	)
}

export default TaskPage

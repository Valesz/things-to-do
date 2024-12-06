import {useNavigate, useParams, useSearchParams} from 'react-router-dom'
import {useCallback, useEffect, useMemo, useRef, useState} from 'react'
import TaskBlock from './components/list/taskBlock'
import {Divider} from 'primereact/divider'
import SubmissionListing from '../submission/components/list/submissionListing'
import {Toast} from 'primereact/toast'
import {TabPanel} from 'primereact/tabview'
import AddSubmissionComponent from '../submission/components/add/addSubmissionComponent'
import SubmissionBlock from '../submission/components/list/submissionBlock'
import DynamicTabView from '../../components/dynamicTabView'
import {openDialog, updateDeleteButtons} from '../../utils/constants/buttons'
import {useAuth} from '../../hooks/useAuth'
import {ConfirmDialog} from 'primereact/confirmdialog'
import ModifySubmissionComponent from '../submission/components/modify/modifySubmissionComponent'
import GradeSubmissionComponent from '../submission/components/grade/gradeSubmissionComponent'
import SubmissionDeleteComponent from '../submission/components/delete/submissionDeleteComponent'
import {useTasks} from './hooks/useTask'
import {useSubmissions} from '../submission/hooks/useSubmissions'
import {FloatLabel} from 'primereact/floatlabel'
import {Button} from 'primereact/button'
import {AutoComplete} from 'primereact/autocomplete'
import NotFoundPage from '../error/notFoundPage'

const TaskPage = () => {
	const {taskId, solutionId, activeTab} = useParams()
	const toastRef = useRef()
	const deleteSubmissionRef = useRef()
	const [searchParams, setSearchParams] = useSearchParams()
	const submissionParams = useMemo(() => {
		return {
			submitterName: searchParams.get('submitterName')
		}
	}, [searchParams])
	const [tasks, taskFetchError, isTaskLoading] = useTasks({
		id: taskId,
		enabled: !!taskId,
		toastRef: toastRef
	})
	const task = useMemo(() => tasks?.[0], [tasks])
	const [submissions, submissionsError, isSubmissionsLoading, setSubmissionsValid] = useSubmissions({
		taskId: taskId,
		...submissionParams,
		enabled: !!taskId,
		toastRef: toastRef
	})
	const [filteredSubmissions, setFilteredSubmissions] = useState([])
	const [submitterNameFilter, setSubmitterNameFilter] = useState(searchParams.get('submitterName') || '')
	const [activeTabIndex, setActiveTabIndex] = useState(0)
	const [extraSubmissionIds, setExtraSubmissionIds] = useState(solutionId && activeTab === 'solutions' ? [parseInt(solutionId)] : [])
	const [submissionModifyDialogVisible, setSubmissionModifyDialogVisible] = useState(false)
	const [modifySubmissionObject, setModifySubmissionObject] = useState({description: ''})
	const [gradingMode, setGradingMode] = useState(false)
	const navigate = useNavigate()
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

		solutionId && activeTab === 'solutions' && setActiveTabIndex(2)

		//eslint-disable-next-line
	}, [activeTab])

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

	const submissionsFilter = useCallback((event) => {
		setFilteredSubmissions([
			...submissions.filter((submission, i, self) =>
				i === self.findIndex(value => submission.submittername === value.submittername)
			).filter((submission) =>
				submission.submittername.toLowerCase().startsWith(event.query.toLowerCase())
			)
		])
	}, [submissions])

	const gradingButton = useMemo(() => {
		return [{
			label: gradingMode ? 'Finish grading' : 'Grade solutions!',
			onClick: () => setGradingMode(prevState => !prevState),
			severity: gradingMode ? 'warning' : 'normal'
		}]
	}, [gradingMode])

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

	const submissionTabTemplate = useCallback((id) => (
		<TabPanel header={`Solution ${id}`} key={id} closable={true}>
			{
				gradingMode ?
					<GradeSubmissionComponent
						submission={submissions?.find(submission => submission.id === parseInt(id))}
						enabled={gradingMode}
						onSuccess={() => setSubmissionsValid(false)}
						toastRef={toastRef}
					/> :
					<SubmissionBlock
						submission={submissions?.find(submission => submission.id === parseInt(id))}
						buttons={
							user && submissions?.find(submissions => submissions.id === parseInt(id))?.submitterid === user.id ?
								updateDeleteButtons('Modify Solution', 'Delete submission',
									(submission) => openDialog(setSubmissionModifyDialogVisible, () => setModifySubmissionObject(submission)),
									(id) => deleteSubmissionRef.current.deleteSubmissionCallback(id))
								: undefined
						}
						titleShow={'submitter'}
					/>
			}
		</TabPanel>
	), [submissions, user, gradingMode, setSubmissionsValid])

	const baseTabs = useMemo(() => [
		<TabPanel header={'Submit solutions'} key={-1}>
			{
				task &&
				<AddSubmissionComponent
					task={task}
					onSuccess={() => {
						setActiveTabIndex(1)
						setSubmissionsValid(false)
					}}
					toastRef={toastRef}
				/>
			}
		</TabPanel>,
		<TabPanel header={'View solutions'} key={-2}>
			{
				(submissionsError && errorDisplay(submissionsError))
				|| (isSubmissionsLoading && loadingDisplay)
				||
				<div className={'flex flex-column align-items-center lg:align-items-start justify-content-center'}>
					<form onSubmit={(e) => e.preventDefault()} className={'w-full p-inputgroup mt-3'}>
						<Button icon={'pi pi-search'}
							onClick={() => {
								submitterNameFilter ?
									searchParams.set('submitterName', submitterNameFilter) :
									searchParams.delete('submitterName')
								setSearchParams([...searchParams.entries()])
							}}
							type={'submit'}
						/>
						<FloatLabel className={'w-full'}>
							<AutoComplete
								id={'submitterNameFilter'}
								className={'w-full'}
								value={submitterNameFilter}
								suggestions={filteredSubmissions}
								field={'submittername'}
								completeMethod={submissionsFilter}
								onChange={(e) => setSubmitterNameFilter(e.target.value)}
							/>
							<label htmlFor={'submitterNameFilter'}>Submitter name</label>
						</FloatLabel>
					</form>
					<SubmissionListing
						submissionList={submissions}
						titleShow={'submitter'}
						buttons={submissionButtons}
						paginatorPosition={'bottom'}
					/>
				</div>
			}
		</TabPanel>
	], [submissions, task, submissionButtons, setSubmissionsValid, isSubmissionsLoading, errorDisplay, loadingDisplay, submitterNameFilter, searchParams, setSearchParams, submissionsError, filteredSubmissions, submissionsFilter])

	if ((solutionId && !parseInt(solutionId)) || (taskId && !parseInt(taskId))
		|| (activeTab && (activeTab !== 'solutions' && activeTab !== 'submit'))) {
		return (
			<NotFoundPage/>
		)
	}

	return (
		<div className={'p-component w-full px-3 lg:px-6 mt-4'} style={{height: 'fit-content', maxHeight: '100vh'}}>
			<div className={'w-full grid grid-nogutter surface-card h-full'}>
				<div className={'col-12 lg:col-6'}>
					{
						(taskFetchError && errorDisplay(taskFetchError))
						|| (isTaskLoading && loadingDisplay)
						||
						<TaskBlock task={task} index={0} buttons={user?.id === task?.ownerid ? gradingButton : undefined}/>
					}
				</div>
				<Divider layout={'vertical'} className={'hidden lg:block p-0 m-0'}/>
				<Divider layout={'horizontal'} className={'block lg:hidden p-0 mt-0'}/>
				<div className={'col-12 lg:col-6 px-0 lg:px-4'}>
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
				</div>
			</div>
			<ConfirmDialog dismissableMask={true}/>
			<ModifySubmissionComponent
				setVisible={setSubmissionModifyDialogVisible}
				visible={submissionModifyDialogVisible}
				onSuccess={() => setSubmissionsValid(false)}
				toastRef={toastRef}
				submission={modifySubmissionObject}
			/>
			<SubmissionDeleteComponent
				ref={deleteSubmissionRef}
				toastRef={toastRef}
				onSuccess={(id) => {
					setExtraSubmissionIds(prevState => [...prevState.filter(_id => _id !== id)])
					setActiveTabIndex(prevState => prevState - 1)
					setSubmissionsValid(false)
				}}
			/>
			<Toast ref={toastRef}/>
		</div>
	)
}

export default TaskPage

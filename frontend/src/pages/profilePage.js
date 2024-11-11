import {useParams} from 'react-router-dom'

import {useCallback, useEffect, useRef, useState} from 'react'
import {fetchUser} from '../services/userService'
import {useAuth} from '../contexts/AuthContext'
import ProfileBlock from '../components/profile/profileBlock'
import {Toast} from 'primereact/toast'
import TaskListing from '../components/task/list/taskListing'
import {deleteTask, fetchTasks} from '../services/taskService'
import {TabPanel, TabView} from 'primereact/tabview'
import {confirmDialog, ConfirmDialog} from 'primereact/confirmdialog'
import ModifyTaskComponent from '../components/task/modify/modifyTaskComponent'

function ProfilePage() {
	const [user, token] = useAuth();
	const [localUser, setLocalUser] = useState(null);
	const { id } = useParams();
	const toastRef = useRef(null);
	const [tasks, setTasks] = useState([]);
	const [modifyVisible, setModifyVisible] = useState(false);
	const [modifyTask, setModifyTask] = useState({
		name: "",
		keywords: [],
		description: "",
	});

	const getUserCallback = useCallback(async () => {
		if (id === undefined) {
			setLocalUser(user);
			return;
		}

		await fetchUser({id: id})
			.then(userData => {
				setLocalUser(userData[0]);
			})
			.catch(error => {
				toastRef.current.show({severity: 'error', summary: "Couldn't load user", detail: error})
			});

	}, [id, user]);

	useEffect(() => {
		getUserCallback();
	}, [getUserCallback])

	const getTasksOfUser = useCallback(async () => {
		if (!localUser) {
			return;
		}

		console.log("Fetching tasks")

		await fetchTasks({ownerId: localUser.id})
			.then((tasks) => {
				setTasks(tasks);
			})
			.catch(error => {
				toastRef.current.show({severity: 'error', summary: "Failed to get tasks", detail: error});
			});
		// eslint-disable-next-line
	}, [localUser]);

	useEffect(() => {
		getTasksOfUser();
	}, [getTasksOfUser]);

	const deleteTaskCallback = useCallback(async (id) => {
		if (!id) {
			return;
		}

		await deleteTask({authToken: token, id: id})
			.then(async () => {
				console.log(tasks);
				await setTasks(() => {
					const index = tasks.findIndex((task) => task.id === id);
					if (index > -1) {
						return tasks.toSpliced(index, 1);
					}
					return tasks;
				})
				toastRef.current.show({severity: 'success', summary: `Deletion successful`});
			})
			.catch(error => {
				toastRef.current.show({severity: 'error', summary: 'Deletion failed', detail: error});
			});
	}, [token, tasks])

	const confirmDelete = (id) => {
		confirmDialog({
			message: "Are you sure you want to delete this task?",
			header: <span className={"text-red-400 bold"}>Delete Confirmation</span>,
			icon: "pi pi-times-circle text-red-400",
			defaultFocus: "reject",
			acceptClassName: "p-button-danger",
			accept: () => deleteTaskCallback(id),
		});
	};

	const openModify = (task) => {
		setModifyTask(task);
		setModifyVisible(true);
	}

	const ownButtons = [
		{
			label: "Modify Task",
			onClick: openModify,
			param: "all",
		},
		{
			label: "Delete Task",
			severity: "danger",
			onClick: confirmDelete,
			param: "id",
		}
	]

	const viewButtons = [
		{
			label: "Submit Solution",
			className: "bg-green-500"
		},
		{
			label: "View Solutions",
		}
	]

	return (
		<div>
			<ProfileBlock user={localUser} />
			{ localUser &&
				<TabView className={"px-2 lg:px-8"} pt={{panelContainer: {className: "p-0"}}}>
					<TabPanel header={"Tasks created"}>
						<TaskListing
							taskList={tasks}
							buttons={(user && user.id === localUser.id && ownButtons) || viewButtons}
							className={"col-12 mx-auto"}
						/>
					</TabPanel>
				</TabView>
			}
			<ConfirmDialog />
			<ModifyTaskComponent
				visible={modifyVisible}
				setVisible={setModifyVisible}
				toastRef={toastRef}
				setTasks={setTasks}
				task={modifyTask}
			/>
			<Toast ref={toastRef} />
		</div>
	);
}

export default ProfilePage;

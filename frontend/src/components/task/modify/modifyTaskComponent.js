import {useCallback, useEffect, useState} from 'react'
import {useAuth} from '../../../contexts/AuthContext'
import AddTaskVisual from '../add/addTaskVisual'
import {updateTask} from '../../../services/taskService'

const ModifyTaskComponent = ({ visible, setVisible, toastRef, setTasks, task}) => {

	const [localName, setLocalName] = useState(task.name);
	const [localKeywords, setLocalKeywords] = useState(task.keywords);
	const [localDescription, setLocalDescription] = useState(task.description);

	const [, token] = useAuth();

	useEffect(() => {
		setLocalName(task.name);
		setLocalKeywords(task.keywords);
		setLocalDescription(task.description);
	}, [task])

	const modifyCallback = useCallback(async () => {
		if (!token) {
			return undefined;
		}

		//TODO: Don't send keyword change every time!
		// console.log(localKeywords === task.keywords ? localKeywords : undefined)
		await updateTask({
			authToken: token,
			id: task.id,
			name: localName,
			description: localDescription,
			keywords: localKeywords,
		}).then(task => {
			setTasks((prevState) => {
				const index = prevState.findIndex((_task) => _task.id === task.id);
				prevState[index] = task;
				setVisible(false);
				return prevState;
			});
			toastRef.current.show({severity: "success", summary: "Update successful"});
		}).catch(error => {
			toastRef.current.show({severity: 'error', summary: "Update Failed", detail: error});
		})
	}, [task.id, localName, localDescription, token, toastRef, setTasks, setVisible, localKeywords])

	return (
		<AddTaskVisual
			name={localName}
			setName={setLocalName}
			keywords={localKeywords}
			setKeywords={setLocalKeywords}
			description={localDescription}
			setDescription={setLocalDescription}
			visible={visible}
			setVisible={setVisible}
			toastRef={toastRef}
			addTaskCallback={modifyCallback}
			submitButton={{label: "Update Task"}}
			title={"Modify your task!"}
		/>
	);
}

export default ModifyTaskComponent;

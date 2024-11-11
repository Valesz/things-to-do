import {useCallback, useState} from 'react'
import {addTasks} from '../../../services/taskService'
import AddTaskVisual from './addTaskVisual'
import {useAuth} from '../../../contexts/AuthContext'

const AddTaskComponent = ({ visible, setVisible, toastRef, setTasks}) => {

	const [name, setName] = useState("");
	const [keywords, setKeywords] = useState([]);
	const [description, setDescription] = useState("");
	//TODO: ADD
	// const [mainTaskId, setMainTaskId] = useState(null);

	const [user, token] = useAuth();

	const addTaskCallback = useCallback(async () => {
		await addTasks({
			authToken: token,
			name: name,
			description: description,
			mainTaskId: null,
			ownerId: user.id,
			timeofcreation: new Date(),
			keywords: keywords,
		})
			.then(async (task) => {
				setTasks((prev) => [task, ...prev])
				setVisible(false);
			}).catch((taskAdditionError) => {
				toastRef.current.show({severity: "error", summary: "Task addition error", detail: taskAdditionError.message})
			});
	}, [name, keywords, description, setTasks, setVisible, toastRef, token, user.id]);

	return (
		<AddTaskVisual
			name={name}
			setName={setName}
			keywords={keywords}
			setKeywords={setKeywords}
			description={description}
			setDescription={setDescription}
			visible={visible}
			setVisible={setVisible}
			addTaskCallback={addTaskCallback}
			toastRef={toastRef}
			submitButton={{label: "Create Task"}}
			title={"Create your own task!"}
		/>
	);
}

export default AddTaskComponent;
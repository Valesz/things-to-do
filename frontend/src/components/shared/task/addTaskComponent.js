import {Dialog} from 'primereact/dialog'
import {FloatLabel} from 'primereact/floatlabel'
import {InputText} from 'primereact/inputtext'
import {Chips} from 'primereact/chips'
import {useState} from 'react'
import {InputTextarea} from 'primereact/inputtextarea'
import {Button} from 'primereact/button'
import {useCookies} from 'react-cookie'
import {serverEndpoint} from '../../../config/server-properties'

const AddTaskComponent = ({ visible, setVisible, toastRef, setTasks}) => {

	const [name, setName] = useState("");
	const [keywords, setKeywords] = useState([]);
	const [description, setDescription] = useState("");
	const [mainTaskId, setMainTaskId] = useState(null);

	const [cookies] = useCookies(['authToken']);

	const requestOptionsTask = {
		method: "POST",
		headers: {
			'Content-Type': 'application/json',
			'Authorization': "Bearer " + cookies.authToken,
		},
		body: JSON.stringify({
			name: name,
			description: description,
			mainTaskId: mainTaskId,
			timeofcreation: new Date().toLocaleDateString("hu-HU", {year: "numeric", month: "numeric", day: "numeric"})
				.replaceAll(". ", "-")
				.replace(".", ""),

		})
	}

	const addTask = async () => {

		let task;
		await fetch(serverEndpoint + "/api/task/", requestOptionsTask)
			.then(async (response) => {
				const isJson = response.headers.get("content-type")?.includes("application/json");
				task = isJson && await response.json();

				if (response.status !== 201) {
					const error = (task && task.message) || response.status;
					await Promise.reject(error);
				}

				if (task) {
					await Promise.resolve();
				}
			})
			.catch((error) => {
				console.log(error);
				toastRef.current.show({severity: 'error', detail: 'Task creation error', description: error});
			})

		if (task) {
			const requestOptionsKeywords = {
				method: "POST",
				headers: {
					'Content-Type': 'application/json',
					'Authorization': "Bearer " + cookies.authToken,
				},
				body: JSON.stringify(keywords.map((keyword) => {
					return {
						keyword: keyword,
						taskid: task.id,
					}
				}))
			}

			await fetch(serverEndpoint + "/api/task/keyword/", requestOptionsKeywords)
				.then(async (response) => {
					const isJson = response.headers.get("content-type")?.includes("application/json");
					const data = isJson && await response.json();

					if (response.status !== 201) {
						const error = (data && data.message) || response.status;
						await Promise.reject(error);
					}

					if (data) {
						task.keywords = data.map((keyword) => keyword.keyword);
						setTasks((prev) => [task, ...prev]);
						setVisible(false);
						await Promise.resolve();
					}
				})
				.catch((error) => {
					console.log(error);
				})
		}
	}

	const header = (
		<span className={"text-2xl"}>Create your task!</span>
	);

	const content = (
		<div className={"flex flex-column surface-50 min-w-max"}>
			<div className={"flex flex-column gap-3"}>
				<div className={"col-12"}>
					<div className={'flex flex-column xl:align-items-start gap-5'}>
						<div className={"grid grid-nogutter w-full pt-3 column-gap-3 md:column-gap-8"}>
							<span className={'text-1xl font-bold text-left'}>
								<FloatLabel>
									<InputText className={"h-3rem w-full md:w-30rem"} maxLength={80} id="name" value={name} onChange={(e) => setName(e.target.value)}/>
									<label htmlFor="name">Task name</label>
								</FloatLabel>
							</span>
							<span className={'text-400 text-right hidden md:block'}>
								{new Date().toLocaleDateString("hu-HU", {year: "numeric", month: "numeric", day: "numeric"})
									.replaceAll(". ", "-")
									.replace(".", "")}
							</span>
						</div>
						<span>
							<FloatLabel>
								<Chips className={"w-full"}
									id="keywords"
									value={keywords}
									allowDuplicate={false}
									onAdd={
										(e) =>
											e.value.length < 23
												? setKeywords([...keywords, e.value])
												: toastRef.current.show({severity: 'error', detail: 'Too Long keyword'})
									}
									onRemove={(e) => {
										setKeywords((prevState) => {
											const index = prevState.indexOf(e.value);
											if (index > -1) {
												prevState.splice(index, 1);
											}
											return prevState;
										})
									}}
									pt={{container: {className: "w-full md:w-30rem lg:max-h-15rem overflow-y-auto"}}}
								/>
								<label htmlFor="keywords">Keywords</label>
							</FloatLabel>
						</span>
						<span>
							<FloatLabel>
								<InputTextarea id="description" className={"w-full md:w-30rem"} value={description} onChange={(e) => setDescription(e.target.value)}/>
								<label htmlFor="description">Description</label>
							</FloatLabel>
						</span>
						<div className={'flex flex-row gap-2 flex-grow-1 lg:h-3rem'}>
							<Button onClick={addTask} label={"Create Task"}/>
						</div>
					</div>
				</div>
			</div>
		</div>
	);

	return (
		<Dialog
			onHide={() => setVisible(false)} visible={visible}
			modal
			header={header}
			resizable={false}
		>
			{content}
		</Dialog>
	);
}

export default AddTaskComponent;
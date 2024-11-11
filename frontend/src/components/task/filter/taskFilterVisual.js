import {FloatLabel} from 'primereact/floatlabel'
import {InputText} from 'primereact/inputtext'
import {Chips} from 'primereact/chips'
import {Calendar} from 'primereact/calendar'
import {TriStateCheckbox} from 'primereact/tristatecheckbox'
import {Button} from 'primereact/button'

const TaskFilterVisual = ({name, setName, creatorName, setCreatorName, keywords, setKeywords, date, setDate, completed, setCompleted, disabled, fetchTasksCallback, toastRef}) => {
	return (
		<>
			<div className={"flex flex-column gap-5 h-min"}>
				<FloatLabel>
					<InputText className={"w-16rem"} id="taskName" value={name} onChange={(e) => setName(e.target.value)} />
					<label htmlFor="taskName">Task Name</label>
				</FloatLabel>
				<FloatLabel>
					<InputText className={"w-16rem"} id="creatorName" value={creatorName} onChange={(e) => setCreatorName(e.target.value)} />
					<label htmlFor="creatorName">Creator Name</label>
				</FloatLabel>
				<FloatLabel>
					<Chips className={"w-16rem"}
						id="keywords"
						value={keywords}
						allowDuplicate={false}
						onAdd={
							(e) =>
								e.value.length < 23
									? setKeywords([...keywords, e.value])
									: toastRef.current.show({severity: 'error', summary: 'Too Long keyword'})
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
						pt={{container: {className: "w-16rem lg:max-h-15rem overflow-y-auto"}}}
					/>
					<label htmlFor="keywords">Keywords</label>
				</FloatLabel>
				<FloatLabel>
					<Calendar className={"w-16rem"} id="date" dateFormat={"yy-mm-dd"} value={date} selectionMode={'range'} onChange={(e) => setDate(e.value)} hideOnRangeSelection={true} readOnlyInput={true} touchUI />
					<label htmlFor="date">Date</label>
				</FloatLabel>
				<div className={"grid grid-nogutter"}>
					<TriStateCheckbox inputId="completed" className={"col-1"} value={completed} onChange={(e) => setCompleted(disabled ? null : e.value)} />
					<label htmlFor="completed" className={"col-10 ml-2 w-14rem"}>
						Completion status: <br/>
						{(completed === null || completed === ""
							? "Ignored"
							: completed === true
								? "Completed"
								: "Uncompleted")}
					</label>
				</div>
			</div>
			<Button
				label={"Search"}
				className={"w-full mt-4"}
				onClick={fetchTasksCallback}
			/>
		</>
	);
}

export default TaskFilterVisual;
import {FloatLabel} from 'primereact/floatlabel'
import {InputText} from 'primereact/inputtext'
import {formatDate} from '../../../utils/utilityService'
import {Chips} from 'primereact/chips'
import {InputTextarea} from 'primereact/inputtextarea'
import {Button} from 'primereact/button'
import {Dialog} from 'primereact/dialog'

const AddTaskVisual = ({name, setName, keywords, setKeywords, description, setDescription, addTaskCallback, visible, setVisible, toastRef, submitButton, title}) => {
	const header = (
		<span className={"text-2xl"}>{title}</span>
	);

	const content = (
		<div className={"flex flex-column surface-50 min-w-max"}>
			<div className={"flex flex-column gap-3"}>
				<div className={"col-12"}>
					<div className={'flex flex-column xl:align-items-start gap-5'}>
						<div className={"grid grid-nogutter w-full pt-3 column-gap-3 md:column-gap-8"}>
							<span className={'text-1xl font-bold text-left'}>
								<FloatLabel>
									<InputText className={"h-3rem max-w-20rem md:max-w-full w-full md:w-30rem"} maxLength={80} id="name" value={name} onChange={(e) => setName(e.target.value)}/>
									<label htmlFor="name">Task name</label>
								</FloatLabel>
							</span>
							<span className={'text-400 text-right hidden md:block'}>
								{formatDate(new Date())}
							</span>
						</div>
						<span>
							<FloatLabel>
								<Chips className={"w-full max-w-20rem md:max-w-full"}
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
									pt={{container: {className: "w-full max-w-20rem md:max-w-full md:w-30rem lg:max-h-15rem overflow-y-auto"}}}
								/>
								<label htmlFor="keywords">Keywords</label>
							</FloatLabel>
						</span>
						<span>
							<FloatLabel>
								<InputTextarea id="description" className={"w-full max-w-20rem md:max-w-full md:w-30rem"} value={description} onChange={(e) => setDescription(e.target.value)}/>
								<label htmlFor="description">Description</label>
							</FloatLabel>
						</span>
						<div className={'flex flex-row gap-2 flex-grow-1 lg:h-3rem'}>
							<Button onClick={addTaskCallback} label={submitButton.label}/>
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

export default AddTaskVisual
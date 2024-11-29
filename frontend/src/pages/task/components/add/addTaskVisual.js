import {Button} from 'primereact/button'
import {Dialog} from 'primereact/dialog'
import FormComponent from '../../../../components/form/formComponent'
import PropTypes from 'prop-types'

const AddTaskVisual = ({formData, setFormData, addTaskCallback, visible, setVisible, toastRef, title, submitButton}) => {
	const header = title && (
		<span className={'text-2xl'}>{title}</span>
	)

	const content = (
		<div className={'flex flex-column surface-50 min-w-max'}>
			<div className={'flex flex-column gap-3'}>
				<div className={'col-12'}>
					<FormComponent
						formData={formData}
						setFormData={setFormData}
						submitButton={
							submitButton || <Button label={'Create Task'}/>
						}
						toastRef={toastRef}
						action={addTaskCallback}
						className={'flex flex-column gap-4 mt-3'}
						buttonsClassName={'mt-3'}
					/>
				</div>
			</div>
		</div>
	)

	return (
		<Dialog
			onHide={() => setVisible(false)} visible={visible}
			modal
			header={header}
			resizable={false}
			dismissableMask={true}
		>
			{content}
		</Dialog>
	)
}

export default AddTaskVisual

AddTaskVisual.propTypes = {
	formData: PropTypes.object.isRequired,
	setFormData: PropTypes.func.isRequired,
	addTaskCallback: PropTypes.func,
	visible: PropTypes.bool.isRequired,
	setVisible: PropTypes.func.isRequired,
	toastRef: PropTypes.object.isRequired,
	title: PropTypes.string,
	submitButton: PropTypes.element
}

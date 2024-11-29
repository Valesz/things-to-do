import FormComponent from '../../../../components/form/formComponent'
import PropTypes from 'prop-types'

const TaskFilterVisual = ({filterFormData, setFilterFormData, submitButton, action, toastRef}) => {
	return <FormComponent
		formData={filterFormData}
		setFormData={setFilterFormData}
		toastRef={toastRef}
		submitButton={submitButton}
		action={action}
		className={'flex flex-column gap-4'}
	/>
}

export default TaskFilterVisual

TaskFilterVisual.propTypes = {
	filterFormData: PropTypes.object.isRequired,
	setFilterFormData: PropTypes.func.isRequired,
	submitButton: PropTypes.element,
	action: PropTypes.func,
	toastRef: PropTypes.object.isRequired
}

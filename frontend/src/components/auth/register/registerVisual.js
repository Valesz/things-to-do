import {Button} from 'primereact/button'
import {Card} from 'primereact/card'
import FormComponent from '../../form/formComponent'
import PropTypes from 'prop-types'

const RegisterVisual = ({formData, setFormData, registerCallback, extraButtons}) => {
	const header = (
		<h1 className={'border-x-3 border-primary text-center'}>Register</h1>
	)

	return (
		<Card header={header} className={'w-10 max-w-max'}>
			<FormComponent
				formData={formData}
				setFormData={setFormData}
				className={'flex flex-column gap-4'}
				buttonsClassName={'flex justify-content-between mt-2'}
				submitButton={
					<Button type={'submit'} icon={'pi pi-user-plus'} label={'Register'}/>
				}
				extraButtons={extraButtons}
				action={registerCallback}
			/>
		</Card>
	)
}

export default RegisterVisual

RegisterVisual.propTypes = {
	formData: PropTypes.object.isRequired,
	setFormData: PropTypes.func.isRequired,
	registerCallback: PropTypes.func.isRequired,
	extraButtons: PropTypes.arrayOf(PropTypes.object)
}

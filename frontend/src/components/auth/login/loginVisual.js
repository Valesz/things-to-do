import {Button} from 'primereact/button'
import {Card} from 'primereact/card'
import FormComponent from '../../form/formComponent'
import PropTypes from 'prop-types'

const LoginVisual = ({formData, setFormData, loginCallback, className, extraButtons}) => {
	const header = (
		<h1 className={'border-x-3 border-primary text-center'}>Login</h1>
	)

	return (
		<Card header={header} className={className || 'w-10 max-w-max'}>
			<FormComponent
				formData={formData}
				setFormData={setFormData}
				submitButton={
					<Button type={'submit'} icon={'pi pi-user'} label={'Login'}/>
				}
				extraButtons={extraButtons}
				className={'flex flex-column gap-4'}
				buttonsClassName={'flex justify-content-between mt-2'}
				action={loginCallback}
			/>
		</Card>
	)
}

export default LoginVisual

LoginVisual.propTypes = {
	formData: PropTypes.object.isRequired,
	setFormData: PropTypes.func.isRequired,
	loginCallback: PropTypes.func.isRequired,
	className: PropTypes.string,
	extraButtons: PropTypes.arrayOf(PropTypes.object)
}

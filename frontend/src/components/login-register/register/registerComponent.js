import {useCallback, useState} from 'react'
import {register} from '../../../services/authService'
import RegisterVisual from './registerVisual'
import PropTypes from 'prop-types'

const RegisterComponent = ({toastRef, extraButtons}) => {
	const [registerError, setRegisterError] = useState('')

	const [formData, setFormData] = useState(
		{
			username: {
				label: 'Username',
				name: 'username',
				value: '',
				type: 'text'
			},
			email: {
				label: 'Email',
				name: 'email',
				value: '',
				type: 'text',
				textType: 'email'
			},
			password: {
				label: 'Password',
				name: 'password',
				value: '',
				type: 'text',
				textType: 'password'
			}
		}
	)

	const registerCallback = useCallback(async () => {
		await register(formData.username.value, formData.email.value, formData.password.value)
			.then((user) => {
				setRegisterError(null)
				console.log(user)
				toastRef.current.show({severity: 'success', summary: 'Registered successfully', detail: 'Please login to your account!'})
			})
			.catch(error => {
				setRegisterError(error)
				toastRef.current.show({severity: 'error', summary: 'Register Error', detail: error.message})
			})
	}, [formData, toastRef])

	return (
		<RegisterVisual
			registerCallback={registerCallback}
			registerError={registerError}
			formData={formData}
			setFormData={setFormData}
			extraButtons={extraButtons}
		/>
	)
}

export default RegisterComponent

RegisterComponent.propTypes = {
	toastRef: PropTypes.object.isRequired,
	extraButtons: PropTypes.arrayOf(PropTypes.object)
}

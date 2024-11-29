import {useCallback, useState} from 'react'
import {register} from '../../../services/authService'
import RegisterVisual from './registerVisual'
import PropTypes from 'prop-types'
import {setInvalidAttributeToFromElements} from '../../../utils/utilityService'
import {emailRegexPattern, passwordRegexPattern, usernameRegexPattern} from '../../../utils/constants/regex'

const RegisterComponent = ({toastRef, extraButtons}) => {
	const [formData, setFormData] = useState(
		{
			username: {
				label: 'Username',
				name: 'username',
				value: '',
				type: 'text',
				invalidCallback: (value) => !usernameRegexPattern.test(value)
			},
			email: {
				label: 'Email',
				name: 'email',
				value: '',
				type: 'text',
				textType: 'email',
				invalidCallback: (value) => !emailRegexPattern.test(value)
			},
			password: {
				label: 'Password',
				name: 'password',
				value: '',
				type: 'text',
				textType: 'password',
				invalidCallback: (value) => !passwordRegexPattern.test(value)
			}
		}
	)

	const registerCallback = useCallback(async () => {

		if (!formData.username.value || !formData.email.value || !formData.password.value) {
			toastRef.current.show({severity: 'error', summary: 'Registration error', detail: 'Please complete all of the fields'})
			setInvalidAttributeToFromElements(formData, setFormData, {
				username: {invalid: true},
				email: {invalid: true},
				password: {invalid: true}
			})
			return
		}

		if (!emailRegexPattern.test(formData.email.value)) {
			toastRef.current.show({severity: 'error', summary: 'Registration error', detail: 'Invalid email address'})
			return
		}

		if (!passwordRegexPattern.test(formData.password.value)) {
			toastRef.current.show({severity: 'error', summary: 'Registration error', detail: 'Invalid password'})
			return
		}

		await register(formData.username.value, formData.email.value, formData.password.value)
			.then(() => {
				toastRef.current.show({severity: 'success', summary: 'Registered successfully', detail: 'Please login to your account!'})
				setInvalidAttributeToFromElements(formData, setFormData, {
					username: {invalid: false},
					email: {invalid: false},
					password: {invalid: false}
				})
			})
			.catch(error => {
				toastRef.current.show({severity: 'error', summary: 'Register Error', detail: error.message})
				setInvalidAttributeToFromElements(formData, setFormData, {
					username: {invalid: true},
					email: {invalid: true},
					password: {invalid: true}
				})
			})
	}, [formData, toastRef])

	return (
		<RegisterVisual
			registerCallback={registerCallback}
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

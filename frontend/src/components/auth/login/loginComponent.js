import {useState, useCallback} from 'react'
import {useNavigate} from 'react-router-dom'
import {useAuth} from '../../../hooks/useAuth'
import LoginVisual from './loginVisual'
import PropTypes from 'prop-types'
import {setInvalidAttributeToFromElements} from '../../../utils/utilityService'

const LoginComponent = ({toastRef, className, extraButtons}) => {
	const navigate = useNavigate()

	const [, , loginAction] = useAuth()

	const [formData, setFormData] = useState({
		username: {
			label: 'Username',
			name: 'username',
			value: '',
			type: 'text'
		},
		password: {
			label: 'Password',
			name: 'password',
			value: '',
			type: 'text',
			textType: 'password'
		}
	})

	const loginCallback = useCallback(async () => {
		await loginAction(formData.username.value, formData.password.value)
			.then((loggedInUser) => {
				navigate(`/profile/${loggedInUser.id}`)
				setInvalidAttributeToFromElements(formData, setFormData, {
					username: {invalid: false},
					password: {invalid: false}
				})
			})
			.catch((error) => {
				toastRef.current.show({
					severity: 'error',
					summary: 'Login Error',
					detail: error.message
				})
				setInvalidAttributeToFromElements(formData, setFormData, {
					username: {invalid: true},
					password: {invalid: true}
				})
			})
	}, [loginAction, navigate, formData, toastRef])

	return (
		<LoginVisual
			loginCallback={loginCallback}
			toastRef={toastRef}
			className={className}
			extraButtons={extraButtons}
			formData={formData}
			setFormData={setFormData}
		/>
	)
}

export default LoginComponent

LoginComponent.propTypes = {
	toastRef: PropTypes.object.isRequired,
	className: PropTypes.string,
	extraButtons: PropTypes.arrayOf(PropTypes.object)
}

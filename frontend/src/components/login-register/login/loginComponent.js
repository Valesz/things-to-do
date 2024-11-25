import {useState, useCallback} from 'react'
import {useNavigate} from 'react-router-dom'
import {useAuth} from '../../../contexts/AuthContext'
import LoginVisual from './loginVisual'
import PropTypes from 'prop-types'

const LoginComponent = ({toastRef, className, extraButtons}) => {
	const [loginError, setLoginError] = useState('')

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
			.then(() => {
				setLoginError('')
				navigate('/profile')
			})
			.catch((error) => {
				setLoginError(error)
				toastRef.current.show({
					severity: 'error',
					summary: 'Login Error',
					detail: error.cause === '401' ?
						'invalid username or password' :
						'Unknown error'
				})
			})
	}, [loginAction, navigate, formData, toastRef])

	return (
		<LoginVisual
			loginCallback={loginCallback}
			loginError={loginError}
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

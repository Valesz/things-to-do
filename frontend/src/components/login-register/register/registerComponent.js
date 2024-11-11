import {useCallback, useState} from 'react'
import {register} from '../../../services/authService'
import RegisterVisual from './registerVisual'

const RegisterComponent = ({toastRef}) => {
	const [usernameInput, setUsernameInput] = useState('');
	const [emailInput, setEmailInput] = useState('');
	const [passwordInput, setPasswordInput] = useState('');

	const [registerError, setRegisterError] = useState('');

	const registerCallback = useCallback(async () => {
		await register(usernameInput, emailInput, passwordInput)
			.then((user) => {
				setRegisterError(null);
				console.log(user);
				toastRef.current.show({severity: 'success', summary: 'Registered successfully', detail: "Please login to your account!"});
			})
			.catch(error => {
				setRegisterError(error);
				toastRef.current.show({severity: 'error', summary: 'Register Error', detail: error});
			})
	}, [usernameInput, emailInput, passwordInput, toastRef])

	return (
		<RegisterVisual
			registerCallback={registerCallback}
			registerError={registerError}
			usernameInput={usernameInput}
			setUsernameInput={setUsernameInput}
			emailInput={emailInput}
			setEmailInput={setEmailInput}
			passwordInput={passwordInput}
			setPasswordInput={setPasswordInput}
		/>
	);
}

export default RegisterComponent;
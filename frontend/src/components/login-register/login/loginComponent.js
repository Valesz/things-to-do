import {useState, useCallback} from 'react'
import {useNavigate} from 'react-router-dom'
import {useAuth} from '../../../contexts/AuthContext'
import LoginVisual from './loginVisual'

const LoginComponent = ({toastRef}) => {
	const [usernameInput, setUsernameInput] = useState('');
	const [passwordInput, setPasswordInput] = useState('');

	const [loginError, setLoginError] = useState('');

	const navigate = useNavigate();

	const [,, loginAction] = useAuth();

	const loginCallback = useCallback(async () => {
		await loginAction(usernameInput, passwordInput)
			.then(() => {
				setLoginError('');
				navigate("/profile");
			})
			.catch((error) => {
				setLoginError(error);
				toastRef.current.show({
					severity: "error",
					summary: "Login Error",
					detail: error === 401 ?
						"invalid username or password" :
						"Unknown error",
				})
			})
	}, [usernameInput, passwordInput, loginAction, navigate, toastRef]);

	return (
		<LoginVisual
			loginCallback={loginCallback}
			loginError={loginError}
			usernameInput={usernameInput}
			setUsernameInput={setUsernameInput}
			passwordInput={passwordInput}
			setPasswordInput={setPasswordInput}
			toastRef={toastRef}
		/>
	);
}

export default LoginComponent;
import {FloatLabel} from 'primereact/floatlabel'
import {InputText} from 'primereact/inputtext'
import {Card} from 'primereact/card'
import {useState, useRef, useCallback} from 'react'
import {Button} from 'primereact/button'
import {Toast} from 'primereact/toast'
import {useCookies} from 'react-cookie'
import {useNavigate} from 'react-router-dom'
import {login} from '../../services/authService'

const LoginComponent = () => {
	const [usernameInput, setUsernameInput] = useState('');
	const [passwordInput, setPasswordInput] = useState('');

	const [loginError, setLoginError] = useState('');
	const toastRef = useRef(null);

	const [_, setCookie] = useCookies(['authToken']);
	const navigate = useNavigate();

	const loginCallback = useCallback(async () => {
		await login(usernameInput, passwordInput)
			.then((token) => {
				setCookie('authToken', token, {path: "/", sameSite: 'strict'});
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
	}, [usernameInput, passwordInput]);

	const header = (
		<h1 className={"border-x-3 border-primary text-center"}>Login</h1>
	);

	const footer = (
		<Button icon={"pi pi-user"} label={"Login"} onClick={loginCallback}/>
	);

	return (
		<Card header={header} footer={footer} className={"w-10 max-w-max"}>
			<div className={"flex flex-column gap-4"}>
				<FloatLabel className={"flex flex-column gap-2"}>
					<InputText id={'username'} invalid={loginError} placeholder={'Enter your username'} value={usernameInput} onChange={(e) => setUsernameInput(e.target.value)}/>
					<label htmlFor={'username'}>Username</label>
				</FloatLabel>

				<FloatLabel className={"flex flex-column gap-2"}>
					<InputText id={'password'} invalid={loginError} type={"password"} placeholder={'Enter your password'} value={passwordInput} onChange={(e) => setPasswordInput(e.target.value)}/>
					<label htmlFor={"password"}>Password</label>
				</FloatLabel>
			</div>
			<Toast ref={toastRef} />
		</Card>
	);
}

export default LoginComponent;
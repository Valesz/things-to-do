import {FloatLabel} from 'primereact/floatlabel'
import {InputText} from 'primereact/inputtext'
import {Card} from 'primereact/card'
import {useState, useRef} from 'react'
import {Button} from 'primereact/button'
import {Toast} from 'primereact/toast'
import {serverEndpoint} from '../../../config/server-properties'
import {useCookies} from 'react-cookie'
import {useNavigate} from 'react-router-dom'

const LoginComponent = () => {
	const [usernameInput, setUsernameInput] = useState('');
	const [passwordInput, setPasswordInput] = useState('');

	const [loginError, setLoginError] = useState('');
	const toastRef = useRef(null);

	const [cookies, setCookie] = useCookies(['authToken']);
	const navigate = useNavigate();

	const handleLogin = async () => {
		const requestOptions = {
			method: "POST",
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify({
				username: usernameInput,
				password: passwordInput
			})
		};

		await fetch(serverEndpoint + "/api/auth/login", requestOptions)
			.then(async response => {
				const isText = response.headers.get('content-type')?.includes('text/plain');
				const data = isText && await response.text();

				if (!response.ok) {
					const error = (data && data.message) || response.status;
					return Promise.reject(error);
				}

				if (data) {
					setCookie('authToken', data, {path: "/", sameSite: 'strict'})
					setLoginError('')
					navigate("/profile")
				}

			})
			.catch(error => {
				setLoginError(error);
				toastRef.current.show({
					severity: 'error',
					summary: "Login Error",
					detail: error === 401 ?
						"Invalid username or password" :
						"Unknown error"
				})
			})
	};

	const header = (
		<h1 className={"border-x-3 border-primary text-center"}>Login</h1>
	);

	const footer = (
		<Button icon={"pi pi-user"} label={"Login"} onClick={handleLogin}/>
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
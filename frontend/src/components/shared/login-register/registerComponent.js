import {FloatLabel} from 'primereact/floatlabel'
import {InputText} from 'primereact/inputtext'
import {Card} from 'primereact/card'
import {useRef, useState} from 'react'
import {Button} from 'primereact/button'
import {Toast} from 'primereact/toast';
import {serverEndpoint} from '../../../config/server-properties'

const RegisterComponent = () => {
	const [usernameInput, setUsernameInput] = useState('');
	const [emailInput, setEmailInput] = useState('');
	const [passwordInput, setPasswordInput] = useState('');

	const [registerError, setRegisterError] = useState('');
	const toastRef = useRef(null);

	const handleRegister = async () => {
		console.log("Register...")
		const requestOptions = {
			method: "POST",
			headers: {
				"Content-Type" : "application/json",
			},
			body: JSON.stringify({
				username: usernameInput,
				email: emailInput,
				timeofcreation: new Date().toLocaleDateString("en-CA"),
				status: "AKTIV",
				password: passwordInput,
			})
		};

		await fetch(serverEndpoint + "/api/auth/register", requestOptions)
			.then(async response => {
				const isJson = response.headers.get("content-type")?.includes("application/json");
				const data = isJson && await response.json();

				if (!response.ok) {
					const error = (data && data.message) || response.status;
					return Promise.reject(error);
				}

				if (data) {
					setRegisterError(null);
					console.log(data);
					toastRef.current.show({severity: 'success', summary: 'Registered successfully', detail: "Please login to your account!"})
				}
			})
			.catch(error => {
				setRegisterError(error);
				toastRef.current.show({severity: 'error', summary: 'Register Error', detail: error});
			})
	}

	const header = (
		<h1 className={"border-x-3 border-primary text-center"}>Register</h1>
	);

	const footer = (
		<Button icon={"pi pi-user-plus"} onClick={handleRegister} label={"Register"} />
	);

	return (
		<Card header={header} footer={footer} className={"w-10 max-w-max"}>
			<div className={"flex flex-column gap-4"}>
				<FloatLabel>
					<InputText id={"username"} placeholder={"Enter your username"} invalid={registerError} value={usernameInput} onChange={(e) => setUsernameInput(e.target.value)} />
					<label htmlFor={"username"}>Username</label>
				</FloatLabel>

				<FloatLabel>
					<InputText id={"email"} type={"email"} placeholder={"Enter your email address"} invalid={registerError} value={emailInput} onChange={(e) => setEmailInput(e.target.value)} />
					<label htmlFor={"email"}>Email</label>
				</FloatLabel>

				<FloatLabel>
					<InputText id={"password"} type={"password"} placeholder={"Enter your password"} invalid={registerError} value={passwordInput} onChange={(e) => setPasswordInput(e.target.value)} />
					<label htmlFor={"password"}>Password</label>
				</FloatLabel>
			</div>
			<Toast ref={toastRef} />
		</Card>
	);
}

export default RegisterComponent;
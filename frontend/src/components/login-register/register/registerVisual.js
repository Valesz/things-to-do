import {Button} from 'primereact/button'
import {Card} from 'primereact/card'
import {FloatLabel} from 'primereact/floatlabel'
import {InputText} from 'primereact/inputtext'

const RegisterVisual = ({registerCallback, registerError, usernameInput, setUsernameInput, emailInput, setEmailInput, passwordInput, setPasswordInput}) => {
	const header = (
		<h1 className={"border-x-3 border-primary text-center"}>Register</h1>
	);

	const footer = (
		<Button icon={"pi pi-user-plus"} onClick={registerCallback} label={"Register"} />
	);

	return (
		<Card header={header} footer={footer} className={"w-10 max-w-max"}>
			<div className={"flex flex-column gap-4"}>
				<FloatLabel>
					<InputText id={"register-username"} placeholder={"Enter your username"} invalid={registerError} value={usernameInput} onChange={(e) => setUsernameInput(e.target.value)} />
					<label htmlFor={"register-username"}>Username</label>
				</FloatLabel>

				<FloatLabel>
					<InputText id={"register-email"} type={"email"} placeholder={"Enter your email address"} invalid={registerError} value={emailInput} onChange={(e) => setEmailInput(e.target.value)} />
					<label htmlFor={"register-email"}>Email</label>
				</FloatLabel>

				<FloatLabel>
					<InputText id={"register-password"} type={"password"} placeholder={"Enter your password"} invalid={registerError} value={passwordInput} onChange={(e) => setPasswordInput(e.target.value)} />
					<label htmlFor={"register-password"}>Password</label>
				</FloatLabel>
			</div>
		</Card>
	);
}

export default RegisterVisual;
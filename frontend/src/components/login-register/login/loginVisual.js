import {Button} from 'primereact/button'
import {Card} from 'primereact/card'
import {FloatLabel} from 'primereact/floatlabel'
import {InputText} from 'primereact/inputtext'

const LoginVisual = ({loginCallback, loginError, usernameInput, setUsernameInput, passwordInput, setPasswordInput}) => {
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
					<InputText id={'login-username'} invalid={loginError} placeholder={'Enter your username'} value={usernameInput} onChange={(e) => setUsernameInput(e.target.value)}/>
					<label htmlFor={'login-username'}>Username</label>
				</FloatLabel>

				<FloatLabel className={"flex flex-column gap-2"}>
					<InputText id={'login-password'} invalid={loginError} type={"password"} placeholder={'Enter your password'} value={passwordInput} onChange={(e) => setPasswordInput(e.target.value)}/>
					<label htmlFor={"login-password"}>Password</label>
				</FloatLabel>
			</div>
		</Card>
	);
}

export default LoginVisual;

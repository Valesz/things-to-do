import LoginComponent from '../components/login-register/loginComponent'
import {Divider} from 'primereact/divider'
import RegisterComponent from '../components/login-register/registerComponent'

const LoginPage = () => {
	return (
		<div className={"flex justify-content-center align-items-center h-screen flex-column md:flex-row"}>
			<LoginComponent />
			<div className={"h-2 md:h-full w-full md:w-2"}>
				<Divider layout="vertical" className="hidden md:flex">
					<b>OR</b>
				</Divider>
				<Divider layout="horizontal" className="flex md:hidden" align="center">
					<b>OR</b>
				</Divider>
			</div>
			<RegisterComponent />
		</div>
	);
}

export default LoginPage;
import LoginComponent from '../components/login-register/login/loginComponent'
import {Divider} from 'primereact/divider'
import RegisterComponent from '../components/login-register/register/registerComponent'
import {useRef} from 'react'
import {Toast} from 'primereact/toast'

const LoginPage = () => {
	const toastRef = useRef()

	return (
		<div className={'flex justify-content-center align-items-center h-screen flex-column md:flex-row mt-6 md:mt-0'}>
			<LoginComponent toastRef={toastRef}/>
			<div className={'h-2 md:h-full w-full md:w-2'}>
				<Divider layout="vertical" className="hidden md:flex">
					<b>OR</b>
				</Divider>
				<Divider layout="horizontal" className="flex md:hidden" align="center">
					<b>OR</b>
				</Divider>
			</div>
			<RegisterComponent toastRef={toastRef}/>
			<Toast ref={toastRef}/>
		</div>
	)
}

export default LoginPage
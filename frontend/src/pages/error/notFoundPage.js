import {Card} from 'primereact/card'
import {useMemo} from 'react'
import {Button} from 'primereact/button'
import {useNavigate} from 'react-router-dom'

const NotFoundPage = () => {
	const navigate = useNavigate()

	const header = useMemo(() => (
		<div className={'flex justify-content-center align-items-center gap-4'}>
			<i className={'pi pi-exclamation-triangle text-4xl text-red-400'}/>
			<h1 className={'text-center'}>404 Page not found!</h1>
			<i className={'pi pi-exclamation-triangle text-4xl text-red-400'}/>
		</div>
	), [])

	const footer = useMemo(() => (
		<Button label={'Go to home page'} icon={'pi pi-home text-xl'} onClick={() => navigate('/')}/>
	), [navigate])

	return (
		<div className={'w-full flex justify-content-center align-items-center'} style={{height: '90vh'}}>
			<Card header={header} footer={footer} className={'w-5 text-center'}>
				<span>This page was not found on the server, please make sure you entered the url correctly!</span>
			</Card>
		</div>
	)
}

export default NotFoundPage
import {Card} from 'primereact/card'
import {Button} from 'primereact/button'

function HomePage() {
	return (
		<Card title={'Simple Card'}>
			<p className={''}>
				Home works!
			</p>
			<Button label={'Home works!'} rounded/>
		</Card>
	)
}

export default HomePage

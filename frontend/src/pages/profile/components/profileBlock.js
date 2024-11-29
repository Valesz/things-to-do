import {Button} from 'primereact/button'
import {Card} from 'primereact/card'
import PropTypes from 'prop-types'
import {userStatusEnum} from '../../../utils/constants/userEnums'

const ProfileBlock = ({user, title, buttons}) => {

	if (!user) {
		return (
			<div className={'px-5 flex align-items-center justify-content-center'} style={{height: 'fit-content'}}>
				<Card
					className={'w-full md:w-full lg:w-5'}
				>
					<h1 className={'text-center'}>Profile not found!</h1>
					<p className={'p-component text-center'}>The profile does not exist in our system, please make sure you went to the right page!</p>
				</Card>
			</div>
		)
	}

	const header = (
		<h1 className={'border-x-3 border-primary text-center'}>{title}</h1>
	)

	const footer = () => (
		<div className={'flex flex-column w-full sm:w-5 gap-2 justify-content-center mx-auto'}>
			{
				buttons?.map?.(button => (
					<Button
						key={button.label}
						label={button.label}
						rounded
						severity={button.severity}
						className={button.className}
						onClick={button.onClick && (() => {
							button.onClick(button.param && user[button.param])
						})}
					/>
				))
			}
		</div>
	)

	return (
		<div className={'px-5 flex align-items-center justify-content-center my-8'} style={{height: 'fit-content'}}>
			<Card className={'w-full md:w-full lg:w-5'} header={header} footer={buttons ? footer : undefined}>
				<div className={'grid'}>
					<div className={'col-6 text-right'}>
						Username:
					</div>
					<div className={'col-6'}>
						{user.username}
					</div>
					<div className={'col-6 text-right'}>
						Email:
					</div>
					<div className={'col-6'}>
						{user.email}
					</div>
					<div className={'col-6 text-right'}>
						Joined on:
					</div>
					<div className={'col-6'}>
						{user.timeofcreation}
					</div>
					<div className={'col-6 text-right'}>
						Classification:
					</div>
					<div className={'col-6'}>
						{
							(user.classification && (user.classification > 0.5 ? 'Group 1' : 'Group 2')) || 'Unidentifiable'
						}
					</div>
					<div className={'col-6 text-right'}>
						Precision:
					</div>
					<div className={'col-6'}>
						{
							user.precisionofanswers ? user.precisionofanswers * 100 + '%' : 'Unidentifiable'
						}
					</div>
					{
						user?.status === userStatusEnum.INACTIVE &&
						<span className={'col-12 text-red-400 text-center p-0 m-0 text-2xl font-bold mt-5'}>This profile is no longer active</span>
					}
				</div>
			</Card>
		</div>
	)
}

export default ProfileBlock

ProfileBlock.propTypes = {
	user: PropTypes.object,
	title: PropTypes.string,
	buttons: PropTypes.arrayOf(PropTypes.object)
}

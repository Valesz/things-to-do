import {Button} from 'primereact/button'
import {Ripple} from 'primereact/ripple'
import {Card} from 'primereact/card'

const ProfileBlock = ({user}) => {

	if (!user) {
		return (
			<div className={'px-5 flex align-items-center justify-content-center'} style={{height: 'fit-content'}}>
				<Card
					className={'w-full md:w-full lg:w-5'}
				>
					<h1 className={'text-center'}>Profile not found!</h1>
					<p className={"p-component text-center"}>The profile does not exist in our system, please make sure you went to the right page!</p>
				</Card>
			</div>
		);
	}

	const header = (
		<h1 className={'border-x-3 border-primary text-center'}>{user.username}'s Profile</h1>
	);

	const footer = (
		<div className={"flex flex-column w-full sm:w-5 gap-2 justify-content-center mx-auto"}>
			<Button label={'Modify profile'} rounded/>
			<Button label={'Delete profile'} severity={"danger"} rounded/>
			<Ripple/>
		</div>
	);

	return (
		<div className={'px-5 flex align-items-center justify-content-center my-8'} style={{height: 'fit-content'}}>
			<Card className={"w-full md:w-full lg:w-5"} header={header} footer={footer}>
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
						{user.classification ? user.classification > 0.5 ? "Group 1" : "Group 2" : "Unidentifiable"}
					</div>
					<div className={'col-6 text-right'}>
						Precision:
					</div>
					<div className={'col-6'}>
						{user.precisionofanswers ? user.precisionofanswers * 100 + "%" : "Unidentifiable"}
					</div>
				</div>
			</Card>
		</div>
	);
}

export default ProfileBlock;

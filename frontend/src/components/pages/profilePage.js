import {useParams} from 'react-router-dom'

import {Card} from 'primereact/card';
import {Button} from 'primereact/button'
import {useEffect, useState} from 'react'
import {useCookies} from 'react-cookie'
import {serverEndpoint} from '../../config/server-properties'
import {Ripple} from 'primereact/ripple'

function ProfilePage() {
	const [authCookie] = useCookies(["authToken"])
	const [user, setUser] = useState();
	const { id } = useParams();

	const requestOptions = {
		method: "GET",
		headers: {
			'Content-Type': 'application/json',
			'Authorization': "Bearer " + authCookie.authToken,
		},
	};

	useEffect(  () => {

		const fetchUserById = async () => await fetch(
			id !== undefined ?
				serverEndpoint + "/api/user/?id=" + id :
				serverEndpoint + "/api/user/token?token=" + authCookie.authToken,
			requestOptions
		);

		fetchUserById()
			.then(async response => {
				const isJson = response.headers.get("content-type")?.includes("application/json");
				const data = isJson && await response.json();

				if (!response.ok) {
					const error = (data && data.message) || response.status;
					return Promise.reject(error);
				}

				if (data) {
					if (data.length === 0) {
						return;
					}

					setUser(data[0] || data);
					console.log(data[0] || data);
				}
			})
			.catch(error => {
				console.log(error)
			})
	}, [])

	if (user === undefined) {
		return (
			<Card title={"User not found"}>
				<p className={""}>
					User not found
				</p>
				<Button label={"User not Found"} rounded />
			</Card>
		)
	}

	const header = (
		<h1 className={'border-x-3 border-primary text-center'}>{user.username}'s Profile</h1>
	);

	const footer = (
		<div className={"flex flex-column w-full sm:w-5 gap-2 justify-content-center mx-auto"}>
			<Button label={'Modify profile'} rounded/>
			<Button label={'Delete profile'} severity={"danger"} rounded/>
			<Ripple />
		</div>
	);

	return (
		<div className={'px-5 flex align-items-center justify-content-center h-screen'}>
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

export default ProfilePage;

import {useAuth} from '../../../../hooks/useAuth'
import {useCallback, useEffect, useState} from 'react'
import FormComponent from '../../../../components/form/formComponent'
import PropTypes from 'prop-types'
import {Card} from 'primereact/card'
import {Button} from 'primereact/button'
import {updateUser} from '../../sevices/userService'
import {fetchJWTToken} from '../../../../services/authService'
import {setInvalidAttributeToFromElements} from '../../../../utils/utilityService'
import {emailRegexPattern, passwordRegexPattern, usernameRegexPattern} from '../../../../utils/constants/regex'

const ProfileModifyComponent = ({user, toastRef, className, extraButtons, onSuccess}) => {
	const [loggedInUser, token, loginCallback] = useAuth()
	const [formData, setFormData] = useState({
		username: {
			id: 'username',
			name: 'username',
			label: 'Username',
			value: user.username,
			type: 'text',
			invalidCallback: (value) => !usernameRegexPattern.test(value)
		},
		email: {
			id: 'email',
			name: 'email',
			label: 'Email',
			value: user.email,
			type: 'text',
			textType: 'email',
			invalidCallback: (value) => !emailRegexPattern.test(value)
		},
		oldPassword: {
			id: 'oldPassword',
			name: 'oldPassword',
			label: 'Old Password *',
			value: '',
			type: 'text',
			textType: 'password',
			invalid: false
		},
		newPassword: {
			id: 'newPassword',
			name: 'newPassword',
			label: 'New Password',
			value: '',
			type: 'text',
			textType: 'password',
			invalid: false,
			invalidCallback: (value) => !passwordRegexPattern.test(value)
		},
		newPasswordAgain: {
			id: 'newPasswordAgain',
			name: 'newPasswordAgain',
			label: 'New Password Again',
			value: '',
			type: 'text',
			textType: 'password',
			invalid: false,
			invalidCallback: (value) => !passwordRegexPattern.test(value)
		}
	})

	useEffect(() => {
		setFormData(prevState => {
			return {
				...prevState,
				username: {
					...prevState.username,
					value: user.username
				},
				email: {
					...prevState.email,
					value: user.email
				},
				oldPassword: {
					...prevState.oldPassword,
					value: ''
				},
				newPassword: {
					...prevState.newPassword,
					value: ''
				},
				newPasswordAgain: {
					...prevState.newPasswordAgain,
					value: ''
				}
			}
		})
	}, [user])

	const modifyProfileCallback = useCallback(async () => {
		if (user.username === formData.username.value && user.email === formData.email.value && !formData.newPassword.value) {
			toastRef.current.show({severity: 'error', summary: 'Data change error', detail: 'You didn\'t change anything...'})
			setInvalidAttributeToFromElements(formData, setFormData, {
				username: {invalid: true},
				email: {invalid: true},
				newPassword: {invalid: true},
				newPasswordAgain: {invalid: true}
			})
			return
		}

		if (!formData.oldPassword.value) {
			toastRef.current.show({severity: 'error', summary: 'Data change error', detail: 'Please provide your current password to change your data'})
			setInvalidAttributeToFromElements(formData, setFormData, {
				oldPassword: {invalid: true},
				newPassword: {invalid: false},
				newPasswordAgain: {invalid: false}
			})
			return
		}

		if (formData.newPassword.value && formData.newPassword.value !== formData.newPasswordAgain.value) {
			toastRef.current.show({severity: 'error', summary: 'Data change error', detail: 'New password and New password again doesn\'t match'})
			setInvalidAttributeToFromElements(formData, setFormData, {
				oldPassword: {invalid: false},
				newPassword: {invalid: true},
				newPasswordAgain: {invalid: true}
			})
			return
		}

		const jwtToken = await fetchJWTToken(user.username, formData.oldPassword.value)
			.catch(error => {
				toastRef.current.show({severity: 'error', summary: 'Bad credentials', detail: error.message === '401' ? 'Invalid username or password' : 'Unknown error'})
				setInvalidAttributeToFromElements(formData, setFormData, {
					oldPassword: {invalid: true},
					newPassword: {invalid: false},
					newPasswordAgain: {invalid: false}
				})
			})

		if (!jwtToken) {
			return
		}

		const updatedUser = await updateUser({
			authToken: token,
			id: user.id,
			username: formData.username.value,
			email: formData.email.value,
			password: formData.newPassword.value ? formData.newPassword.value : undefined,
			timeOfCreation: user.timeofcreation,
			status: user.status,
			classification: user.classification,
			precisionOfAnswers: user.precisionofanswers
		}).catch(error => {
			toastRef.current.show({severity: 'error', summary: 'Data change error', detail: error.message})
		})

		if (!updatedUser) {
			return
		}

		toastRef.current.show({severity: 'success', summary: 'Data changed successfully!', detail: 'You have changed your data successfully!'})
		setInvalidAttributeToFromElements(formData, setFormData, {
			username: {invalid: false},
			email: {invalid: false},
			oldPassword: {invalid: false},
			newPassword: {invalid: false},
			newPasswordAgain: {invalid: false}
		})

		if (loggedInUser.id !== user.id) {
			return
		}

		await loginCallback(updatedUser.username, formData.newPassword.value ? formData.newPassword.value : formData.oldPassword.value)
			.catch(async error => {
				toastRef.current.show({severity: 'error', summary: 'Login error', detail: error.cause})
			})

		onSuccess?.(updatedUser)
	}, [formData, toastRef, token, user, loggedInUser, loginCallback, onSuccess])

	const header = (
		<h1 className={'border-x-3 border-primary text-center'}>{user.username}'s data change</h1>
	)

	return (
		<Card header={header} className={className} pt={{body: {className: 'pb-0'}}}>
			<FormComponent
				setFormData={setFormData}
				formData={formData}
				toastRef={toastRef}
				submitButton={
					<Button label={'Update profile data'} className={'mt-3'}/>
				}
				extraButtons={extraButtons}
				buttonsClassName={'flex flex-column gap-1'}
				action={modifyProfileCallback}
			/>
		</Card>
	)
}

export default ProfileModifyComponent

ProfileModifyComponent.propTypes = {
	user: PropTypes.object.isRequired,
	toastRef: PropTypes.object.isRequired,
	className: PropTypes.string,
	extraButtons: PropTypes.arrayOf(PropTypes.object),
	onSuccess: PropTypes.func
}

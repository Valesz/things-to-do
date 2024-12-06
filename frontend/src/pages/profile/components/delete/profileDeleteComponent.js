import {useNavigate} from 'react-router-dom'
import {forwardRef, useCallback, useImperativeHandle} from 'react'
import {deleteUser} from '../../sevices/userService'
import {useAuth} from '../../../../hooks/useAuth'
import {confirmWarnDialog} from '../../../../utils/constants/buttons'
import PropTypes from 'prop-types'

const ProfileDeleteComponent = forwardRef(({navigationUrl, toastRef, onSuccess}, ref) => {
	const [, token, , logoutCallback] = useAuth()
	const navigate = useNavigate()

	const deleteProfile = useCallback(async (id) => {
		if (!id) {
			throw new Error('ID not given')
		}

		return await deleteUser({authToken: token, id: id})
			.then(() => {
				logoutCallback()
				onSuccess?.(id)

				if (!navigationUrl) {
					return
				}

				navigate(navigationUrl)
				toastRef.current.show({severity: 'success', summary: 'Deletion success', detail: 'Profile deactivated'})
			})
			.catch(error => {
				toastRef.current.show({severity: 'error', summary: 'Deletion failed', detail: error.cause})
			})
	}, [token, navigationUrl, navigate, toastRef, logoutCallback, onSuccess])

	useImperativeHandle(ref, () => ({
		deleteProfile(userId) {
			confirmWarnDialog({
				message: 'Are you sure you would like to delete your profile? (Note: this will only deactivate your profile)',
				headerMessage: 'Deletion confirmation',
				classname: 'w-3',
				acceptCallback: () => deleteProfile(userId)
			})
		}
	}))

})

export default ProfileDeleteComponent

ProfileDeleteComponent.propTypes = {
	navigationUrl: PropTypes.string,
	toastRef: PropTypes.object.isRequired,
	onSuccess: PropTypes.func
}

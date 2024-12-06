import {createContext, useCallback, useEffect, useMemo, useState} from 'react'
import {useCookies} from 'react-cookie'
import {fetchUserByAuthToken} from '../pages/profile/sevices/userService'
import {fetchJWTToken} from '../services/authService'
import PropTypes from 'prop-types'
import {userStatusEnum} from '../utils/constants/userEnums'

export const AuthContext = createContext()

const AuthProvider = ({children}) => {
	const [user, setUser] = useState(null)
	const [cookies, setCookie] = useCookies(['authToken'])

	const setLoggedInUser = useCallback(async (token) => {
		return await fetchUserByAuthToken(token)
			.then(async (userData) => {
				if (userData.status !== userStatusEnum.ACTIVE) {
					throw new Error('Not active profile')
				}

				setUser(userData)
				setCookie('authToken', token, {path: '/', sameSite: 'strict'})
				return await Promise.resolve(userData)
			})
			.catch(async error => {
				throw new Error(error)
			})
	}, [setCookie])

	const loginAction = useCallback(async (username, password) => {
		return await fetchJWTToken(username, password)
			.then(async token => {
				return await Promise.resolve(setLoggedInUser(token))
			})
			.catch(async error => {
				throw new Error(error.message, {cause: error})
			})
	}, [setLoggedInUser])

	const logoutAction = useCallback(() => {
		setUser(null)
		setCookie('authToken', '', {sameSite: 'strict', path: '/', maxAge: 0})
		return true
	}, [setUser, setCookie])

	useEffect(() => {
		if (cookies.authToken) {
			setLoggedInUser(cookies.authToken)
				.catch(error => {
					if (error === 401) {
						logoutAction()
					}
				})
		}
	}, [cookies.authToken, setLoggedInUser, logoutAction])

	const authVariables = useMemo(() => [
		user,
		cookies.authToken,
		loginAction,
		logoutAction
	], [user, cookies.authToken, loginAction, logoutAction])

	return (
		<AuthContext.Provider value={authVariables}>
			{children}
		</AuthContext.Provider>
	)
}

export default AuthProvider

AuthProvider.propTypes = {
	children: PropTypes.any.isRequired
}

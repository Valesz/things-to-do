import {createContext, useCallback, useContext, useEffect, useMemo, useState} from 'react'
import {useCookies} from 'react-cookie'
import {fetchUserByAuthToken} from '../services/userService'
import {fetchJWTToken} from '../services/authService'
import PropTypes from 'prop-types'

const AuthContext = createContext()

const AuthProvider = ({children}) => {
	const [user, setUser] = useState(null)
	const [cookies, setCookie] = useCookies(['authToken'])

	const setLoggedInUser = useCallback(async (token) => {
		await fetchUserByAuthToken(token)
			.then((userData) => {
				setUser(userData)
				setCookie('authToken', token, {path: '/', sameSite: 'strict'})
			})
			.catch(async error => {
				return await Promise.reject(new Error(error))
			})
	}, [setCookie])

	const loginAction = useCallback(async (username, password) => {
		await fetchJWTToken(username, password)
			.then(async token => {
				await setLoggedInUser(token)
			})
			.catch(async error => {

				if (error.message === '401') {
					throw new Error(error, {cause: error.message})
				}

				throw new Error('Unknown error', {cause: error})
			})
	}, [setLoggedInUser])

	const logoutAction = useCallback(() => {
		setUser(null)
		setCookie('authToken', '', {sameSite: 'strict', path: '/', maxAge: 0})
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

export const useAuth = () => {
	return useContext(AuthContext)
}

AuthProvider.propTypes = {
	children: PropTypes.any.isRequired
}

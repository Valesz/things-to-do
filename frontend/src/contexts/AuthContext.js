import {createContext, useCallback, useContext, useEffect, useState} from 'react'
import {useCookies} from 'react-cookie'
import {fetchUserByAuthToken} from '../services/userService'
import {fetchJWTToken} from '../services/authService'

const AuthContext = createContext();

const AuthProvider = ({children}) => {
	const [user, setUser] = useState(null);
	const [cookies, setCookie] = useCookies(['authToken']);

	const setLoggedInUser = useCallback(async (token) => {
		await fetchUserByAuthToken(token)
			.then((userData) => {
				setUser(userData);
				setCookie('authToken', token, {path: "/", sameSite: 'strict'});
			})
			.catch(async error => {
				return await Promise.reject(error);
			});
	}, [setCookie]);

	const loginAction = async (username, password) => {
		await fetchJWTToken(username, password)
			.then(async token => {
				await setLoggedInUser(token)
			})
			.catch(async error => {
				return await Promise.reject(error);
			});
	};

	const logoutAction = useCallback(async () => {
		await setUser(null);
		await setCookie("authToken", "", {sameSite: 'strict', path: "/", maxAge: 0});
	}, [setUser, setCookie]);

	useEffect(() => {
		if (cookies.authToken) {
			setLoggedInUser(cookies.authToken)
				.catch(error => {
					if (error === 401) {
						logoutAction();
					}
				});
		}
	}, [cookies.authToken, setLoggedInUser, logoutAction]);

	return (
		<AuthContext.Provider value={[user, cookies.authToken, loginAction, logoutAction]}>
			{children}
		</AuthContext.Provider>
	);
}

export default AuthProvider;

export const useAuth = () => {
	return useContext(AuthContext);
}


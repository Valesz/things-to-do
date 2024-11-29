import {useAuth} from '../../contexts/AuthContext'
import {useNavigate} from 'react-router-dom'
import {useEffect} from 'react'

const AuthGuard = ({Component}) => {
	const [user] = useAuth()
	const navigate = useNavigate()

	useEffect(() => {
		if (!user) {
			navigate('/login')
		}
	}, [user, navigate])

	return Component
}

export default AuthGuard
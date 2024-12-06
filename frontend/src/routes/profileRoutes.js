import {Route, Routes} from 'react-router-dom'
import NotFoundPage from '../pages/error/notFoundPage'
import ProfilePage from '../pages/profile/profilePage'

const ProfileRoutes = () => {
	return (
		<Routes>
			<Route path={':id'} element={<ProfilePage/>}/>
			<Route path={'*'} element={<NotFoundPage/>}/>
		</Routes>
	)
}

export default ProfileRoutes
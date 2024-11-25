import Navbar from './components/navbar/navbar'
import {Outlet} from 'react-router-dom'
import {CookiesProvider} from 'react-cookie'
import AuthProvider from './contexts/AuthContext'

function App() {
	return (
		<CookiesProvider>
			<AuthProvider>
				<Navbar/>
				<Outlet/>
			</AuthProvider>
		</CookiesProvider>
	)
}

export default App

import Navbar from './components/navbar/navbar'
import {Outlet} from 'react-router-dom'
import {CookiesProvider} from 'react-cookie'
import AuthProvider from './contexts/AuthContext'
import Footer from './components/footer/footer'

function App() {
	return (
		<CookiesProvider>
			<AuthProvider>
				<header className={"w-full max-w-screen"}>
					<Navbar/>
				</header>
				<main className={"min-h-screen max-w-screen"}>
					<Outlet/>
				</main>
				<footer>
					<Footer />
				</footer>
			</AuthProvider>
		</CookiesProvider>
	)
}

export default App

import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import reportWebVitals from './reportWebVitals'
import {PrimeReactProvider} from 'primereact/api'
import {BrowserRouter, Route, Routes} from 'react-router-dom'
import HomePage from './pages/homePage'
import 'primereact/resources/primereact.min.css'
import './index.css'
import 'primeicons/primeicons.css'
import 'primeflex/primeflex.css'
import 'primereact/resources/themes/lara-dark-cyan/theme.css'
import LoginPage from './pages/loginPage'
import TaskRoutes from './routes/taskRoutes'
import ProfileRoutes from './routes/profileRoutes'
import NotFoundPage from './pages/error/notFoundPage'

const primeReactConfig = {
	ripple: true
}

const root = ReactDOM.createRoot(document.getElementById('root'))
root.render(
	<React.StrictMode>
		<PrimeReactProvider value={primeReactConfig}>
			<BrowserRouter>
				<Routes>
					<Route path={'/'} element={<App/>}>
						<Route index element={<HomePage/>}/>
						<Route path={'task/*'} element={<TaskRoutes/>}/>
						<Route path={'profile/*'} element={<ProfileRoutes/>}/>
						<Route path={'login'} element={<LoginPage/>}/>
						<Route path={'register'} element={<LoginPage/>}/>
						<Route path={'*'} element={<NotFoundPage/>}/>
					</Route>
				</Routes>
			</BrowserRouter>
		</PrimeReactProvider>
	</React.StrictMode>
)

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals()

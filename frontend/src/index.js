import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import reportWebVitals from './reportWebVitals'
import {PrimeReactProvider} from 'primereact/api'
import {createBrowserRouter, RouterProvider} from 'react-router-dom'
import HomePage from './pages/homePage'
import ProfilePage from './pages/profile/profilePage'
import './index.css'
import 'primeicons/primeicons.css'
import 'primeflex/primeflex.css'
import LoginPage from './pages/loginPage'
import TaskListingPage from './pages/task/taskListingPage'
import TaskPage from './pages/task/taskPage'

const routes = [
	{
		path: '/',
		element: <App/>,
		children: [
			{
				path: '/',
				element: <HomePage/>
			},
			{
				path: '/profile/:id',
				element: <ProfilePage/>
			},
			{
				path: '/login',
				element: <LoginPage/>
			},
			{
				path: '/task',
				element: <TaskListingPage/>
			},
			{
				path: '/task/:taskId',
				element: <TaskPage/>
			},
			{
				path: '/task/:taskId/solutions/:solutionId',
				element: <TaskPage/>
			},
			{
				path: '/task/:taskId/:activeTab',
				element: <TaskPage/>
			}
		]
	}
]

const router = createBrowserRouter(routes)

const primeReactConfig = {
	ripple: true
}

const root = ReactDOM.createRoot(document.getElementById('root'))
root.render(
	<React.StrictMode>
		<PrimeReactProvider value={primeReactConfig}>
			<RouterProvider router={router}>
				<App/>
			</RouterProvider>
		</PrimeReactProvider>
	</React.StrictMode>
)

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals()

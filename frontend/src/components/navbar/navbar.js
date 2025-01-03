import {Button} from 'primereact/button'
import {Toolbar} from 'primereact/toolbar'

import {useLocation, useNavigate} from 'react-router-dom'
import {useEffect, useMemo, useState} from 'react'
import SideNavbarComponent from './sideNavbar'
import {ButtonGroup} from 'primereact/buttongroup'

import {useAuth} from '../../hooks/useAuth'
import useScrollPosition from '../../hooks/useScrollPosition'
import Logo from '../Logo'

const NavbarComponent = () => {
	const navigate = useNavigate()
	const [openSidebar, setOpenSidebar] = useState(false)
	const [navItems, setNavItems] = useState([])
	const scrollY = useScrollPosition()
	const location = useLocation()

	const [user, , , logoutAction] = useAuth()

	const items = useMemo(() => [
		{
			id: 'text-home',
			label: 'Home',
			icon: 'pi pi-home',
			show: true,
			command: () => {
				navigate('/')
			}
		},
		{
			id: 'text-profile',
			label: 'Profile',
			icon: 'pi pi-user',
			show: user,
			command: () => {
				navigate(`/profile/${user.id}`)
			}
		},
		{
			id: 'text-task',
			label: 'Tasks',
			icon: 'pi pi-list-check',
			show: true,
			command: () => {
				navigate('/task')
			}
		},
		{
			id: 'login',
			label: 'Login',
			icon: 'pi pi-user',
			show: !user,
			command: () => {
				navigate('/login')
			}
		},
		{
			id: 'logout',
			label: 'Logout',
			icon: 'pi pi-sign-out',
			show: user,
			command: () => {
				navigate('/login')
				logoutAction()
			}
		}
	], [user, logoutAction, navigate])

	useEffect(() => {
		setNavItems(() => {
			return items.map((item) => {
				return item.show ? item : undefined
			})
		})
	}, [items])

	const start = (
		<>
			<button
				style={{background: 'none', border: 'none'}}
				className={'mr-6 hidden lg:block p-unselectable-text grow-on-hover transition-duration-100 cursor-pointer text-lg'}
				onClick={() => navigate('/')}
				tabIndex={0}
			>
				<Logo />
			</button>
			<div className={"lg:hidden w-3rem"}></div>
		</>
	)

	const center = (
		<button
			style={{background: 'none', border: 'none'}}
			className={'flex lg:hidden text-lg p-unselectable-text grow-on-hover transition-duration-100 cursor-pointer'}
			onClick={() => navigate('/')}
			tabIndex={0}
		>
			<Logo />
		</button>
	)

	const end = (
		<>
			<ButtonGroup className={'align-items-center hidden lg:block'}>
				{navItems.map(item => (
					item && <Button key={item.id} visible={item.show} label={item.label} icon={item.icon} text={item.id.startsWith('text')} onClick={item.command}/>
				))}
			</ButtonGroup>
			<Button className={'lg:hidden'} icon={'pi pi-bars'} onClick={() => setOpenSidebar(true)}/>
		</>
	)

	return (
		<>
			<SideNavbarComponent visible={openSidebar} setVisible={setOpenSidebar} items={items}/>
			<Toolbar
				className={'z-5 w-full'}
				start={start}
				center={center}
				end={end}
				style={{
					position: location.pathname === "/" ? 'fixed' : 'sticky',
					top: 0
				}}
				pt={{
					root: {
						style: {
							backgroundColor: `rgba(31,41,55,${location.pathname === "/" ? scrollY / 100 : 1})`,
							borderColor: `rgba(66, 75, 87, ${location.pathname === "/" ? scrollY / 100 : 1})`,
						}
					}
				}}
			/>
		</>
	)
}

export default NavbarComponent

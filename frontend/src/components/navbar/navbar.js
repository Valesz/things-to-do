import {Button} from 'primereact/button'
import {Toolbar} from 'primereact/toolbar';

import {useNavigate} from 'react-router-dom'
import {useEffect, useMemo, useState} from 'react'
import SideNavbarComponent from './sideNavbar'
import {ButtonGroup} from 'primereact/buttongroup'

import 'primereact/resources/themes/lara-dark-teal/theme.css';
import {useAuth} from '../../contexts/AuthContext'

const NavbarComponent = () => {
	const navigate = useNavigate();
	const [openSidebar, setOpenSidebar] = useState(false);
	const [navItems, setNavItems] = useState([]);

	const [user,,, logoutAction] = useAuth();

	const items = useMemo( () => [
		{
			id: 'text-home',
			label: "Home",
			icon: "pi pi-home",
			show: true,
			command: () => {
				navigate("/");
			},
		},
		{
			id: 'text-profile',
			label: "Profile",
			icon: "pi pi-user",
			show: user,
			command: () => {
				navigate("/profile");
			},
		},
		{
			id: "text-task",
			label: "Tasks",
			icon: "pi pi-list-check",
			show: true,
			command: () => {
				navigate("/task");
			}
		},
		{
			id: "login",
			label: "Login",
			icon: "pi pi-user",
			show: !user,
			command: () => {
				navigate("/login");
			},
		},
		{
			id: 'logout',
			label: 'Logout',
			icon: 'pi pi-sign-out',
			show: user,
			command: async () => {
				await navigate("/login");
				await logoutAction();
			}
		}
	], [user, logoutAction, navigate]);

	useEffect(() => {
		setNavItems(() => {
			return items.map((item) => {
				return item.show ? item : undefined;
			})
		});
	}, [items])

	const start = (
		<>
			<p className={'mr-6 hidden lg:block p-unselectable-text grow-on-hover transition-duration-100 cursor-pointer'}
				onClick={() => navigate("/")}
			>
				LOGO
			</p>
			<div className={"w-3rem block lg:hidden"}></div>
		</>
	);

	const center = (
		<p className={'flex lg:hidden p-unselectable-text grow-on-hover transition-duration-100 cursor-pointer'}
			onClick={() => navigate("/")}
		>
			LOGO
		</p>
	);

	const end = (
		<>
			<ButtonGroup className={'align-items-center hidden lg:block'}>
				{navItems.map(item => (
					item && <Button key={item.id} visible={item.show} label={item.label} icon={item.icon} text={item.id.startsWith('text')} onClick={item.command}/>
				))}
			</ButtonGroup>
			<Button className={"flex lg:hidden"} icon={"pi pi-bars"} onClick={() => setOpenSidebar(true)} />
		</>
	)

	return (
		<>
			<SideNavbarComponent visible={openSidebar} setVisible={setOpenSidebar} items={items} />
			<Toolbar className={"sticky w-full z-5 top-0"} start={start} center={center} end={end}/>
		</>
	)
}

export default NavbarComponent;

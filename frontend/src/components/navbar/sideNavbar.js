import {Sidebar} from 'primereact/sidebar'
import {Button} from 'primereact/button'
import {Ripple} from 'primereact/ripple'
import PropTypes from 'prop-types'

const SideNavbarComponent = ({visible, setVisible, items}) => {

	const content = (
		<div className={'min-h-screen flex relative lg:static'}>
			<div id={'app-sidebar-2'} className="surface-section h-screen block flex-shrink-0 absolute lg:static left-0 top-0 z-1 border-right-1 surface-border select-none w-full">
				<div className={'flex flex-column h-full'}>
					<div className="flex align-items-center justify-content-between px-4 pt-3 flex-shrink-0">
						<span>
							<span className={'font-semibold text-2xl text-primary'}>LOGO</span>
						</span>
						<span>
							<Button icon={'pi pi-times'} onClick={() => setVisible(false)} rounded outlined/>
						</span>
					</div>
					<div className="overflow-y-auto">
						<ul className="list-none p-3 m-0">
							{items.map((item, index) => (
								item.show &&
								<li key={item.id}>
									<button
										style={{background: 'none', border: 'none'}}
										onClick={() => {
											item.command()
											setVisible(false)
										}}
										className={'p-ripple p-component text-lg flex align-items-center cursor-pointer p-3 border-round text-800 hover:surface-100 transition-duration-150 transition-colors w-full'}
										tabIndex={index + 1}
									>
										<i className={item.icon + ' text-xl'}></i>
										<span className={'font-medium ml-2'}>{item.label}</span>
										<Ripple/>
									</button>
								</li>
							))}
						</ul>
					</div>
				</div>
			</div>
		</div>
	)

	return (
		<Sidebar
			visible={visible}
			onHide={() => setVisible(false)}
			content={content}
		></Sidebar>
	)
}

export default SideNavbarComponent

SideNavbarComponent.propTypes = {
	visible: PropTypes.bool.isRequired,
	setVisible: PropTypes.func.isRequired,
	items: PropTypes.arrayOf(PropTypes.object)
}

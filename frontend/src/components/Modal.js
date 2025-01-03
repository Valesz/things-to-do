import {classNames} from 'primereact/utils'

const Modal = ({show, children, style, className}) => {
	if (!show) {
		return
	}

	return (
		<div
			style={{
				position: 'absolute',
				top: 0,
				bottom: 0,
				left: 0,
				right: 0,
				backgroundColor: "rgba(0, 0, 0, .7)",
				zIndex: 3,
				...style
			}}
			className={classNames("flex justify-content-center align-items-center w-full h-full", className)}
		>
			{children}
		</div>
	)
}

export default Modal
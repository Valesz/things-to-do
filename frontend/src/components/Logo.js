import {classNames} from 'primereact/utils'

const Logo = ({classname}) => {
	return (
		<span
			className={classNames("text-3xl pacifico", classname)}
		>
			ThingsToDo
		</span>
	)
}

export default Logo
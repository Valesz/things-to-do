import {useEffect, useReducer, useRef} from 'react'
import {TabView} from 'primereact/tabview'
import PropTypes from 'prop-types'

//TODO: Remove reducer, make it state
function reducer(state, action) {
	if (action.type === 'remove') {
		return {
			...state,
			values: [...state.values.filter((value, index) => index !== action.index - action.baseTabLength)]
		}
	} else if (action.type === 'set') {
		return {
			values: action.values,
			maxIndex: state.maxIndex + 1
		}
	}

	throw Error('Unknown action called.')
}

const DynamicTabView = ({activeTabIndex, setActiveTabIndex, tabTemplate, baseTabs, extraTabValues, onTabClose}) => {
	const [extraTabs, dispatch] = useReducer(reducer, {
		maxIndex: 0,
		values: extraTabValues
	})
	const viewPanelRef = useRef()

	useEffect(() => {
		dispatch({type: 'set', values: extraTabValues})
	}, [extraTabValues])

	return (
		<TabView
			ref={viewPanelRef}
			scrollable
			key={extraTabs.maxIndex}
			activeIndex={activeTabIndex}
			onTabChange={(e) => setActiveTabIndex(e.index)}
			onTabClose={(e) => {
				onTabClose(e)
			}}
		>
			{
				[...baseTabs, ...extraTabs.values.map(tabTemplate)]
			}
		</TabView>
	)
}

export default DynamicTabView

DynamicTabView.propTypes = {
	activeTabIndex: PropTypes.number,
	setActiveTabIndex: PropTypes.func,
	tabTemplate: PropTypes.func.isRequired,
	baseTabs: PropTypes.arrayOf(PropTypes.element),
	extraTabValues: PropTypes.arrayOf(PropTypes.any),
	onTabClose: PropTypes.func
}
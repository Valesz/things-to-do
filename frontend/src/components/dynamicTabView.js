import {useEffect, useRef, useState} from 'react'
import {TabView} from 'primereact/tabview'
import PropTypes from 'prop-types'

const DynamicTabView = ({activeTabIndex, setActiveTabIndex, tabTemplate, baseTabs = [], extraTabValues = [], extraTabElements = [], onTabClose}) => {
	const [extraTabs, setExtraTabs] = useState({
		maxIndex: 0,
		values: extraTabValues
	})
	const viewPanelRef = useRef()

	useEffect(() => {
		setExtraTabs(prevState => {
			return {
				maxIndex: prevState.maxIndex + 1,
				values: extraTabValues
			}
		})
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
				[...baseTabs, ...extraTabs.values.map(tabTemplate), ...extraTabElements]
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
	extraTabElements: PropTypes.arrayOf(PropTypes.element),
	onTabClose: PropTypes.func
}
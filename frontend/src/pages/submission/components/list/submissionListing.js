import {useCallback, useRef, useState} from 'react'
import {DataView} from 'primereact/dataview'
import SubmissionBlock from './submissionBlock'
import PropTypes from 'prop-types'

const SubmissionListing = ({submissionList, buttons, className, titleShow = 'both', paginatorPosition = 'both'}) => {
	const [first, setFirst] = useState(0)
	const [rows, setRows] = useState(5)

	const topOfList = useRef(null)

	const listTemplate = useCallback((list) => {
		if (!list || list.length === 0) {
			return null
		}

		return (
			<div>
				<div ref={topOfList}></div>
				<div className={'grid grid-nogutter'}>
					{submissionList.slice(first, first + rows).map((submission, index) => (
						<SubmissionBlock key={submission.id} titleShow={titleShow} submission={submission} index={index} buttons={buttons}/>
					))}
				</div>
			</div>
		)
	}, [submissionList, buttons, first, rows, titleShow])

	return (
		(submissionList.length > 0
			&& <DataView
				value={submissionList}
				listTemplate={listTemplate}
				paginator={true}
				first={first}
				rows={rows}
				onPage={(e) => {
					topOfList.current.scrollIntoView({block: 'center'})
					setFirst(e.first)
					setRows(e.rows)
				}}
				paginatorPosition={paginatorPosition || 'both'}
				pageLinkSize={window.innerWidth >= 576 ? 5 : 3}
				paginatorTemplate={window.innerWidth
				>= 576 ? 'FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown' : 'PrevPageLink PageLinks NextPageLink RowsPerPageDropdown'}
				totalRecords={submissionList.length}
				rowsPerPageOptions={[5, 10, 20]}
				className={className || 'col-11 lg:col-11 mx-auto z-index-1'}
			/>)
		|| <div className={'col-11 lg:col-8 mx-auto z-index-1 surface-50 p-component text-center h-full'}>
			<div className={'flex flex-column justify-content-center h-full p-4 gap-4'}>
				<div>
					<span className={'font-semibold text-2xl text-900'}>No Submissions yet!</span>
				</div>
				<span className={'text-lg'}>Be the first to solve this task by submitting an answer!</span>
			</div>
		</div>
	)
}

export default SubmissionListing

SubmissionListing.propTypes = {
	submissionList: PropTypes.arrayOf(PropTypes.object).isRequired,
	buttons: PropTypes.arrayOf(PropTypes.object),
	className: PropTypes.string,
	titleShow: PropTypes.string,
	paginatorPosition: PropTypes.string
}

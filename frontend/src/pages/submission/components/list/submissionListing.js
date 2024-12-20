import {useCallback, useRef} from 'react'
import SubmissionBlock from './submissionBlock'
import PropTypes from 'prop-types'
import {Paginator} from 'primereact/paginator'

const SubmissionListing = ({submissionList, first, rows, totalRows, onPageChange, buttons, className, titleShow = 'both'}) => {
	const topOfList = useRef(null)

	const listTemplate = useCallback((list) => {
		if (!list || list.length === 0) {
			return null
		}

		return (
			<div>
				<div ref={topOfList}></div>
				<div className={'grid grid-nogutter gap-4'}>
					{submissionList.map((submission, index) => (
						<SubmissionBlock key={submission.id} titleShow={titleShow} submission={submission} index={index} buttons={buttons}/>
					))}
				</div>
			</div>
		)
	}, [submissionList, buttons, titleShow])

	return (
		<div className={className} style={{height: 'fit-content'}}>
			{(submissionList.length > 0
				&&
				<div>
					<Paginator
						first={first}
						rows={rows}
						totalRecords={totalRows}
						rowsPerPageOptions={[5, 10, 20]}
						onPageChange={(e) => {
							topOfList.current.scrollIntoView({block: 'center'})
							onPageChange?.(e)
						}}
						template={{
							layout: window.innerWidth >= 576
								? 'FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown'
								: 'PrevPageLink PageLinks NextPageLink RowsPerPageDropdown'
						}}
					/>
					{
						listTemplate(submissionList)
					}
					<Paginator
						first={first}
						rows={rows}
						totalRecords={totalRows}
						rowsPerPageOptions={[5, 10, 20]}
						onPageChange={(e) => {
							topOfList.current.scrollIntoView({block: 'center'})
							onPageChange?.(e)
						}}
						template={{
							layout: window.innerWidth >= 576
								? 'FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown'
								: 'PrevPageLink PageLinks NextPageLink RowsPerPageDropdown'
						}}
					/>
				</div>)
			|| <div className={'col-11 lg:col-8 mx-auto z-index-1 surface-50 p-component text-center h-full'}>
				<div className={'flex flex-column justify-content-center h-full p-4 gap-4'}>
					<div>
						<span className={'font-semibold text-2xl text-900'}>No Submissions yet!</span>
					</div>
					<span className={'text-lg'}>Be the first to solve this task by submitting an answer!</span>
				</div>
			</div>}
		</div>
	)
}

export default SubmissionListing

SubmissionListing.propTypes = {
	submissionList: PropTypes.arrayOf(PropTypes.object).isRequired,
	buttons: PropTypes.arrayOf(PropTypes.object),
	className: PropTypes.string,
	titleShow: PropTypes.string,
	first: PropTypes.number.isRequired,
	rows: PropTypes.number.isRequired,
	totalRows: PropTypes.number.isRequired,
	onPageChange: PropTypes.func,
}

import {confirmDialog} from 'primereact/confirmdialog'

export const taskViewButtons = (navigate) => [
	{
		label: 'Submit Solution',
		className: 'bg-green-500',
		onClick: (id) => navigate(`/task/${id}/submit`),
		param: 'id'
	},
	{
		label: 'View Solutions',
		onClick: (id) => navigate(`/task/${id}/solutions`),
		param: 'id'
	}
]

export const viewSolutionsForTaskButtons = (navigate) => [
	{
		label: 'View solutions',
		onClick: (id) => navigate(`/task/${id}/solutions`),
		param: 'id'
	}
]

export const updateDeleteButtons = (modifyLabel, deleteLabel, modifyCallback, deleteCallback) => [
	{
		label: modifyLabel,
		onClick: modifyCallback,
		param: 'all'
	},
	{
		label: deleteLabel,
		severity: 'danger',
		onClick: deleteCallback,
		param: 'id'
	}
]

export const confirmDeleteDialog = (id, message, deleteCallback, rejectCallback) => {
	confirmDialog({
		message: message,
		header: <span className={'text-red-400 bold'}>Delete Confirmation</span>,
		icon: 'pi pi-times-circle text-red-400',
		defaultFocus: 'reject',
		acceptClassName: 'p-button-danger',
		accept: () => deleteCallback?.(id),
		reject: rejectCallback
	})
}

export const openDialog = (setVisible, callback) => {
	callback?.()
	setVisible(true)
}
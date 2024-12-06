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

export const viewSolutionForTask = (navigate) => [
	{
		label: 'View Solution',
		onClick: (submission) => navigate(`/task/${submission.taskid}/solutions/${submission.id}`),
		param: 'all'
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

export const confirmSuccessDialog = ({message, headerMessage, acceptCallback, rejectCallback, icon, className}) => {
	confirmDialog({
		message: message,
		header: <span className={'text-green-400 bold'}>{headerMessage}</span>,
		className: className,
		icon: icon || 'pi pi-check text-green-400',
		defaultFocus: 'accept',
		acceptClassName: 'p-button-success',
		accept: acceptCallback,
		reject: rejectCallback
	})
}

export const confirmWarnDialog = ({message, headerMessage, acceptCallback, rejectCallback, icon, classname}) => {
	confirmDialog({
		message: message,
		header: <span className={'text-red-400 bold'}>{headerMessage}</span>,
		className: classname,
		icon: icon || 'pi pi-times-circle text-red-400',
		defaultFocus: 'reject',
		acceptClassName: 'p-button-danger',
		accept: acceptCallback,
		reject: rejectCallback
	})
}

export const openDialog = (setVisible, callback) => {
	callback?.()
	setVisible(true)
}
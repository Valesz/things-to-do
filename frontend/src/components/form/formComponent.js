import {FloatLabel} from 'primereact/floatlabel'
import {InputText} from 'primereact/inputtext'
import {useCallback} from 'react'
import {Chips} from 'primereact/chips'
import {Calendar} from 'primereact/calendar'
import {RadioButton} from 'primereact/radiobutton'
import {Button} from 'primereact/button'
import {InputTextarea} from 'primereact/inputtextarea'
import PropTypes from 'prop-types'

const FormComponent = ({formData, setFormData, submitButton, extraButtons, className, buttonsClassName, toastRef, action}) => {
	const handleChange = useCallback((data) => {
		const {name, value} = data.target
		setFormData({
			...formData,
			[name]: {
				...formData[name],
				value: value
			}
		})
	}, [setFormData, formData])

	const actionCallback = useCallback((e) => {
		e.preventDefault()
		action?.()
	}, [action])

	return (
		<form className={className || 'flex flex-column gap-5 h-min'} onSubmit={actionCallback}>
			{
				formData &&
				Object.keys(formData).map(key => {
					const inputField = formData[key]

					switch (inputField.type) {
						case 'text':
							return (
								<FloatLabel key={inputField.name}>
									<InputText
										className={inputField.className || 'w-16rem'}
										id={inputField.name}
										name={inputField.name}
										value={inputField.value}
										onChange={handleChange}
										type={inputField.textType}
									/>
									<label htmlFor={inputField.name}>{inputField.label}</label>
								</FloatLabel>
							)
						case 'chips':
							return (
								<FloatLabel key={inputField.name}>
									<Chips className={inputField.className || 'w-16rem'}
										id={inputField.name}
										name={inputField.name}
										value={inputField.value}
										allowDuplicate={false}
										onChange={
											(e) =>
												(e.value.length > 0 && e.value[e.value.length - 1].length < 23)
												|| e.value.length <= 0 ?
													handleChange(e) :
													toastRef.current.show({severity: 'error', summary: 'Too Long keyword', detail: 'Keyword must not exceed a length of 23'})}
										pt={{container: {className: inputField.containerClassName || 'w-16rem lg:max-h-15rem overflow-y-auto'}}}
									/>
									<label htmlFor={inputField.name}>{inputField.label}</label>
								</FloatLabel>
							)
						case 'date':
							return (
								<FloatLabel key={inputField.name}>
									<Calendar
										className={inputField.className || 'w-16rem'}
										id={inputField.name}
										name={inputField.name}
										dateFormat={'yy-mm-dd'}
										value={inputField.value}
										selectionMode={inputField.mode ?? 'single'}
										onChange={handleChange}
										hideOnRangeSelection={true}
										readOnlyInput={true}
										touchUI
									/>
									<label htmlFor={inputField.name}>{inputField.label}</label>
								</FloatLabel>
							)
						case 'radio':
							return (
								<div className={'flex flex-column gap-2'} key={inputField.name}>
									<span className={'mb-2'}>{inputField.label}</span>
									{
										inputField.options.map(option => (
											<div className={'flex align-items-center'} key={option.name}>
												<RadioButton
													inputId={option.name}
													name={inputField.name}
													value={option.value}
													onChange={handleChange}
													checked={inputField.value === option.value}
												/>
												<label htmlFor={option.name} className={'ml-2'}>{option.label}</label>
											</div>
										))
									}
								</div>
							)
						case 'textArea':
							return (
								<FloatLabel key={inputField.name}>
									<InputTextarea
										id={inputField.name}
										name={inputField.name}
										className={inputField.className || 'w-full max-w-20rem md:max-w-full md:w-30rem'}
										value={inputField.value}
										onChange={handleChange}
									/>
									<label htmlFor={inputField.name}>{inputField.label}</label>
								</FloatLabel>
							)
						default:
							throw Error('Unknown type for form element')
					}
				})
			}
			<div className={buttonsClassName}>
				{submitButton}
				{
					extraButtons?.map?.(button => (
						<Button
							text
							key={button.label}
							className={button.className}
							icon={button.icon}
							label={button.label}
							onClick={button.onClick}
							type={'button'}
						/>
					))
				}
			</div>
		</form>
	)
}

export default FormComponent

FormComponent.propTypes = {
	formData: PropTypes.object.isRequired,
	setFormData: PropTypes.func.isRequired,
	submitButton: PropTypes.element.isRequired,
	extraButtons: PropTypes.arrayOf(PropTypes.object),
	className: PropTypes.string,
	buttonsClassName: PropTypes.string,
	toastRef: PropTypes.object,
	action: PropTypes.func
}

import {Button} from 'primereact/button'
import PropTypes from 'prop-types'
import FormComponent from '../../../../components/form/formComponent'

const AddSubmissionVisual = ({formData, setFormData, title, submitButton, addSubmissionCallback}) => {

	return (
		<div className={'flex flex-column surface-50'}>
			<div className={'flex flex-column gap-3'}>
				<div>
					<div className={'flex flex-column xl:align-items-start gap-5'}>
						<span className={'text-2xl font-bold'}>{title}</span>
						<span className={'w-full'}>
							<FormComponent
								setFormData={setFormData}
								formData={formData}
								submitButton={
									submitButton || <Button label={'Submit your solution'}/>
								}
								action={addSubmissionCallback}
								className={'flex flex-column w-full gap-4'}
							/>
						</span>
					</div>
				</div>
			</div>
		</div>
	)

}

export default AddSubmissionVisual

AddSubmissionVisual.propTypes = {
	formData: PropTypes.object.isRequired,
	setFormData: PropTypes.func.isRequired,
	title: PropTypes.string,
	submitButton: PropTypes.object,
	addSubmissionCallback: PropTypes.func
}

import {Button} from 'primereact/button'
import Logo from '../components/Logo'
import {useCallback} from 'react'
import {useTasks} from './task/hooks/useTask'
import {Carousel} from 'primereact/carousel'
import Modal from '../components/Modal'
import {ProgressSpinner} from 'primereact/progressspinner'
import TaskBlock from './task/components/list/taskBlock'
import {useNavigate} from 'react-router-dom'
import TaskListing from './task/components/list/taskListing'
import useScrollPosition from '../hooks/useScrollPosition'

function HomePage() {
	const navigate = useNavigate()
	const scrollY = useScrollPosition()
	const [featuredTasks, , isLoading] = useTasks({
		pageNumber: 0,
		pageSize: 7
	})
	const responsiveCarouselOptions = [
		{
			breakpoint: '1400px',
			numVisible: 3,
			numScroll: 1
		},
		{
			breakpoint: '1199px',
			numVisible: 2,
			numScroll: 1
		},
		{
			breakpoint: '575px',
			numVisible: 1,
			numScroll: 1
		}
	];

	const featuredTasksTemplate = useCallback( (task) => {
		return (
			<div style={{height: '20rem'}} className={"sm:mx-2 lg:mx-5"}>
				<TaskBlock task={task} className={"m-0"} style={{backgroundColor: "#192132"}} wrapText={true} />
			</div>
		)
	}, [])

	return (
		<div className={"p-component"}>
			<Modal show={isLoading}>
				<ProgressSpinner />
			</Modal>
			<main className={"top-0 left-0 right-0 relative"}>
				<header
					className={"flex flex-column align-items-center justify-content-center gradient-background"}
					style={{height: '100vh'}}
				>
					<div className={"text-4xl"}>No one get's <span className={"font-semibold"}>bored</span></div>
					<div className={"text-3xl mt-3"}>When there are</div>
					<Logo classname={"text-primary-400 text-6xl underline"}/>
					<Button label={"Do Things!"} style={{top: "20vh"}} onClick={() => navigate("/task")}/>
				</header>
				<section className={"pt-4"} style={{backgroundColor: "#111827"}}>
					<div className={"text-center text-4xl w-full font-bold mb-4"}>Our best tasks for you</div>
					{
						featuredTasks &&
						<Carousel
							value={featuredTasks.tasks}
							numScroll={1}
							numVisible={3}
							itemTemplate={featuredTasksTemplate}
							responsiveOptions={responsiveCarouselOptions}
						/>
					}
				</section>
				<section className={"pt-4"} style={{backgroundColor: "#111827"}}>
					<div
						className={"grid grid-nogutter mx-auto"}
					>
						<div className={"col-12 sm:col-7 md:col-5 align-self-center"}>
							<div
								className={"surface-card p-3 px-5 ml-3 md:ml-8 mr-3 md:mr-0"}
								style={{
									boxShadow: "0 0 10px 4px var(--primary-500)",
									borderRadius: 10
								}}
							>
								<h2 className={"text-3xl mb-6"}>Spend your time with us!</h2>
								<div className={"text-xl"}>
									<p className={"mb-0 line-height-2"}>
										Complete <b>fun</b> and <b>engaging</b> tasks straight from our best creators!
									</p>
									<p className={"mt-3"}>
										With <b>10+</b> tasks waiting just for you there will be always something to do!
									</p>
								</div>
							</div>
						</div>
						<div className={"hidden sm:block md:col-1 lg:col-2"}>

						</div>
						<div
							className={"col-12 sm:col-5 sm:max-w-30rem"}
							style={{
								height: "28rem",
								overflow: 'hidden',
								transform: "skew(-3deg)",
								mask: "linear-gradient(0deg, "
									+ "rgba(255,255,255,0) 0%, "
									+ "rgba(255,255,255,1) 10%, "
									+ "rgba(255,255,255,1) 90%, "
									+ "rgba(255,255,255,0) 100%)"
							}}
						>
							{
								featuredTasks &&
								<TaskListing
									taskList={featuredTasks.tasks}
									totalRows={featuredTasks.tasks.length}
									rows={featuredTasks.tasks.length}
									first={0}
									paginator={false}
									className={"surface-card"}
									style={{
										translate: `0 ${Math.round(-scrollY / 3)}px`
									}}
								/>
							}
						</div>
					</div>
				</section>
				<section className={"pt-4 md:px-8"} style={{backgroundColor: "#111827"}}>
					<div
						className={'grid grid-nogutter mx-auto'}
					>
						<div
							className={'col-12 sm:col-5 sm:max-w-30rem'}
							style={{
								height: '28rem',
								overflow: 'hidden',
								transform: 'skew(3deg)',
								mask: "linear-gradient(0deg, "
									+ "rgba(255,255,255,0) 0%, "
									+ "rgba(255,255,255,1) 10%, "
									+ "rgba(255,255,255,1) 90%, "
									+ "rgba(255,255,255,0) 100%)"
							}}
						>
							{
								featuredTasks &&
								<TaskListing
									taskList={featuredTasks.tasks}
									totalRows={featuredTasks.tasks.length}
									rows={featuredTasks.tasks.length}
									first={0}
									paginator={false}
									className={'surface-card'}
									style={{
										translate: `0 ${Math.round(-scrollY / 3 - 500)}px`
									}}
								/>
							}
						</div>
						<div className={'hidden sm:block md:col-1 lg:col-2'}>

						</div>
						<div className={'col-12 sm:col-7 md:col-5 align-self-center'}>
							<div
								className={'surface-card p-3 px-5 mx-3 md:mx-0'}
								style={{
									boxShadow: '0 0 10px 4px var(--primary-500)',
									borderRadius: 10
								}}
							>
								<h2 className={'text-3xl mb-6'}>Become one of our 10+ creators!</h2>
								<div className={'text-xl'}>
									<p className={'mb-0 line-height-2'}>
										You have a <b>lot to do</b>? Do you need some <b>examples</b> for your task?
									</p>
									<p className={'mt-3'}>
										Upload your task, and get <b>inspired</b> by our <b>community's</b> solutions!
									</p>
								</div>
							</div>
						</div>
					</div>
				</section>
				<section className={"pt-7"} style={{backgroundColor: "#111827"}}>
					<div
						className={"flex justify-content-center align-items-center py-6 text-center"}
						style={{
							height: "5rem",
							backgroundColor: "#0c121f"
						}}
					>
						<span className={"text-2xl md:text-3xl"} >We have <b>{featuredTasks?.totalTasks - 1}+</b> tasks that you might enjoy!</span>
					</div>
				</section>
			</main>
		</div>
	)
}

export default HomePage

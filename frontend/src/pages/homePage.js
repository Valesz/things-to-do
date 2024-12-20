import {Card} from 'primereact/card'
import {Button} from 'primereact/button'
import {Splitter, SplitterPanel} from 'primereact/splitter'
import Logo from '../components/Logo'
import {Suspense, useCallback, useMemo, useState} from 'react'
import {useTasks} from './task/hooks/useTask'
import {Carousel} from 'primereact/carousel'
import Modal from '../components/Modal'
import {ProgressSpinner} from 'primereact/progressspinner'
import TaskBlock from './task/components/list/taskBlock'

function HomePage() {
	const [featuredTasks, , isLoading] = useTasks({
		pageNumber: 0,
		pageSize: 10
	})

	const featuredTasksTemplate = useCallback( (task) => {
		return (
			<div style={{height: "15rem"}} className={"mx-5"}>
				<TaskBlock task={task} className={"m-0"} style={{backgroundColor: "#192132"}} />
			</div>
		)
	}, [])

	if (isLoading) {
		return (
			<Modal show={true}>
				<ProgressSpinner />
			</Modal>
		)
	}

	return (
		<div className={"p-component"}>
			<main className={"top-0 left-0 right-0 absolute"}>
				<header
					className={"flex flex-column align-items-center justify-content-center gradient-background"}
					style={{height: '100vh'}}
				>
					<div className={"text-4xl"}>No one get's <span className={"font-semibold"}>bored</span></div>
					<div className={"text-3xl mt-3"}>When there are</div>
					<Logo classname={"text-primary-400 text-6xl underline"} />
					<Button label={"Do Things!"} style={{top: "20vh"}} />
				</header>
				<section className={"pt-4"} style={{backgroundColor: "#111827"}}>
					<div className={"text-center text-4xl w-full font-bold mb-4"}>Featured tasks</div>
					<Carousel
						value={featuredTasks.tasks}
						numScroll={1}
						numVisible={3}
						itemTemplate={featuredTasksTemplate}
					/>
				</section>
			</main>
		</div>
	)
}

export default HomePage

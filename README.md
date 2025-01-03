# Things-to-do

Things to do is a leetcode like web application 
where users can create whatever tasks they have on their mind 
and others are able to complete those tasks by submitting their solutions
for you to take a look at them and get inspired to get things done!

Things to do is also a great place to spend your boring hours.
Instead of mindlessly scrolling through any social media you could
do something fun and productive!

## Technologies used

- Spring-boot 2
- React 18
- H2 database
- Gradle

## Setting up the project

1. Clone the repository
2. Set up the backend
   1. At the root run `./gradlew build` to build the project
   2. Navigate to `/backend/src/main/resources`
   3. Create an embedded H2 database in the folder
   4. Run the `init.sql` script in the database's console
   5. Paste the Connection URL to the database to into 
   the `application.properties` file's 
   `datasource.url` field and provide the necessary credentials 
   to access the database
   6. Navigate back to root
   7. Run `./gradlew run` to run the built gradle project
   8. You should see a Spring Boot application starting.
3. Set up the frontend
   1. Navigate to `/frontend`
   2. Run `npm install` to install the necessary dependencies
   3. After the dependencies installed run `npm start` and the
   React application should start.
4. Enjoy creating tasks and completing them!

## Origin of the project

Writing the specification and developing this application was a
task assigned to me on my internship at Scriptum Informatics Zrt.

## Future developments

### General

- Fix date usage (replace string to number)
- Adding a logger and monitoring options
- Containerizing the setup
- Somehow figure out load-balancing

### Frontend

- Make it work with Vite
- Replacing PrimeReact with Material UI
- Replacing PrimeFaces with Tailwind CSS
- Replacing fetch API usages to React Query or 
  change it to use graphQL
- Extract the components and other non-page files to separate folder
- Set up ESLint to control the dependencies
- Update to React 19
- Introduce Zustand for better state management
- Somehow figure out how to use Suspense to display modal while fetch is loading
- Change to TypeScript
- Add localization (?)
- Fixing up minor bugs

### Backend (Where do I begin)

- Too much code duplication
- Update to Spring boot 3
- Introduce JPA instead of just JDBC
- Add logging
- Replace @Autowired with final and @RequiredArgsConstructor
- Write more tests
- Understand


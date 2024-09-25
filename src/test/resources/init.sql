DROP TABLE IF EXISTS "keywordsForTasks";
DROP TABLE IF EXISTS "completedTasks";
DROP TABLE IF EXISTS "submission";
DROP TABLE IF EXISTS "task";
DROP TABLE IF EXISTS "user";

// Creating tables
CREATE TABLE "user" (
                        id Integer AUTO_INCREMENT NOT NULL,
                        username VARCHAR(30) NOT NULL,
                        email VARCHAR(30) NOT NULL,
                        timeOfCreation DATE NOT NULL,
                        status VARCHAR(30) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        classification FLOAT NOT NULL,
                        precisionOfAnswers FLOAT NOT NULL
);

CREATE TABLE "task" (
                        id INTEGER auto_increment NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        description VARCHAR(400) NOT NULL,
                        timeOfCreation DATE NOT NULL,
                        mainTaskId INTEGER,
                        ownerId INTEGER NOT NULL
);

CREATE TABLE "submission" (
                              id INTEGER AUTO_INCREMENT NOT NULL,
                              taskId INTEGER NOT NULL,
                              description VARCHAR(400) NOT NULL,
                              timeOfSubmission DATE NOT NULL,
                              acceptance BOOLEAN,
                              submitterId INTEGER NOT NULL
);

CREATE TABLE "completedTasks" (
                                  id INTEGER AUTO_INCREMENT NOT NULL,
                                  userId INTEGER NOT NULL,
                                  taskId INTEGER NOT NULL
);

CREATE TABLE "keywordsForTasks" (
                                    id INTEGER AUTO_INCREMENT NOT NULL,
                                    taskId INTEGER NOT NULL,
                                    keyword VARCHAR(20) NOT NULL
);

// Primary Keys
ALTER TABLE "user" ADD PRIMARY KEY (id);
ALTER TABLE "task" ADD PRIMARY KEY (id);
ALTER TABLE "submission" ADD PRIMARY KEY (id);
ALTER TABLE "completedTasks" ADD PRIMARY KEY (id);
ALTER TABLE "keywordsForTasks" ADD PRIMARY KEY (id);

// Adding Foreign key constraints
ALTER TABLE "task" ADD FOREIGN KEY (ownerId) REFERENCES "user"(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "submission" ADD FOREIGN KEY (taskId) REFERENCES "task"(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "completedTasks" ADD FOREIGN KEY (userId) REFERENCES "user"(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "completedTasks" ADD FOREIGN KEY (taskId) REFERENCES "task"(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "keywordsForTasks" ADD FOREIGN KEY (taskId) REFERENCES "task"(id) ON DELETE CASCADE ON UPDATE CASCADE;

//TODO: Triggerek
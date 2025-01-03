DROP TABLE IF EXISTS "keywordsForTasks";
DROP TABLE IF EXISTS "submission";
DROP TABLE IF EXISTS "task";
DROP TABLE IF EXISTS "user";

// Creating tables
CREATE TABLE "user" (
	ID                 INTEGER AUTO_INCREMENT NOT NULL,
	USERNAME           VARCHAR(30)  NOT NULL,
	EMAIL              VARCHAR(30)  NOT NULL,
	TIMEOFCREATION     DATE         NOT NULL,
	STATUS             VARCHAR(30)  NOT NULL,
	PASSWORD           VARCHAR(255) NOT NULL,
	CLASSIFICATION     FLOAT,
	PRECISIONOFANSWERS FLOAT
);

CREATE TABLE "task" (
	ID             INTEGER AUTO_INCREMENT NOT NULL,
	NAME           VARCHAR(255) NOT NULL,
	DESCRIPTION    VARCHAR(400) NOT NULL,
	TIMEOFCREATION DATE         NOT NULL,
	MAINTASKID     INTEGER,
	OWNERID        INTEGER      NOT NULL
);

CREATE TABLE "submission" (
	ID               INTEGER AUTO_INCREMENT NOT NULL,
	TASKID           INTEGER      NOT NULL,
	DESCRIPTION      VARCHAR(400) NOT NULL,
	TIMEOFSUBMISSION DATE         NOT NULL,
	ACCEPTANCE       VARCHAR(30)  NOT NULL,
	SUBMITTERID      INTEGER      NOT NULL
);

CREATE TABLE "keywordsForTasks" (
	ID      INTEGER AUTO_INCREMENT NOT NULL,
	TASKID  INTEGER     NOT NULL,
	KEYWORD VARCHAR(20) NOT NULL
);

// PRIMARY Keys
ALTER TABLE "user"
ADD PRIMARY KEY (ID);
ALTER TABLE "task"
ADD PRIMARY KEY (ID);
ALTER TABLE "submission"
ADD PRIMARY KEY (ID);
ALTER TABLE "keywordsForTasks"
ADD PRIMARY KEY (ID);

// Adding FOREIGN KEY CONSTRAINTS
ALTER TABLE "task"
ADD FOREIGN KEY (OWNERID) REFERENCES "user"(ID) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "submission"
ADD FOREIGN KEY (TASKID) REFERENCES "task"(ID) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "keywordsForTasks"
ADD FOREIGN KEY (TASKID) REFERENCES "task"(ID) ON DELETE CASCADE ON UPDATE CASCADE;

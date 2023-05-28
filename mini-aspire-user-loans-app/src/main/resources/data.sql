DROP TABLE IF EXISTS LOANS;
DROP TABLE IF EXISTS USERS;
CREATE TABLE USERS(
	ID INT NOT NULL PRIMARY KEY, 
	USER_NAME VARCHAR(50) NOT NULL, 
	PASSWORD VARCHAR(20) NOT NULL,
	USER_ROLE VARCHAR(10));
CREATE TABLE LOANS(
	ID INT NOT NULL PRIMARY KEY, 
	USER_ID INT NOT NULL,
	IS_ACTIVE BOOLEAN, 
	DISBERSED_AMOUNT FLOAT,
	REPAYED_AMOUNT FLOAT,
	TERM_TOTAL INT, 
	TERM_REMAINING INT,
	EMI_DATES VARCHAR(1000),
	REPAYMENTS VARCHAR(1000));
ALTER TABLE LOANS ADD FOREIGN KEY(USER_ID) 
	REFERENCES USERS(ID);
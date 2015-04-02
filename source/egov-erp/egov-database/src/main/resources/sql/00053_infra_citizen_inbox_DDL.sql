CREATE SEQUENCE SEQ_EG_CITIZENINBOX;

CREATE TABLE EG_CITIZENINBOX
(
	ID			integer NOT NULL,
	MODULE_ID		integer NOT NULL,
	MESSAGE_TYPE		character(1),
	IDENTIFIER		character varying(50),
	HEADER_MSG     	 	character varying(500),
	DETAILED_MSG		character varying(2048),
	LINK			character varying(256),
	READ			boolean ,
	MSG_DATE		DATE       NOT NULL,
	STATE_ID		integer NOT NULL,
	ASSIGNED_TO_USER	integer NOT NULL,
	PRIORITY		character(1),
	STATUS			character varying(100),
	CREATEDBY		bigint NOT NULL,
	CREATEDDATE     	timestamp without time zone,
	LASTMODIFIEDDATE	timestamp without time zone,
	LASTMODIFIEDBY		bigint NOT NULL,
	VERSION			bigint NOT NULL
);

ALTER TABLE EG_CITIZENINBOX ADD CONSTRAINT PK_C_INBOX_ID PRIMARY KEY (ID);

ALTER TABLE EG_CITIZENINBOX ADD CONSTRAINT FK_C_INBOX_MOD_ID FOREIGN KEY (MODULE_ID) REFERENCES EG_MODULE (ID_MODULE);

ALTER TABLE EG_CITIZENINBOX ADD CONSTRAINT FK_C_INBOX_STATE_ID FOREIGN KEY (STATE_ID) REFERENCES EG_WF_STATES (ID);

ALTER TABLE EG_CITIZENINBOX ADD CONSTRAINT FK_C_INBOX_CREATEDBY FOREIGN KEY (CREATEDBY) REFERENCES EG_USER (ID);

ALTER TABLE EG_CITIZENINBOX ADD CONSTRAINT FK_C_INBOX_LASTMODIFIEDBY FOREIGN KEY (LASTMODIFIEDBY) REFERENCES EG_USER (ID);
  


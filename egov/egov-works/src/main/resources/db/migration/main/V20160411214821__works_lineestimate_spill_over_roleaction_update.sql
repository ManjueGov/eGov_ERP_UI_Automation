----------------Role Action Mappings for Spill Over Line Estimate Ajax Call for Authority Names-------------------
insert into EG_ACTION (ID,NAME,URL,QUERYPARAMS,PARENTMODULE,ORDERNUMBER,DISPLAYNAME,ENABLED,CONTEXTROOT,VERSION,CREATEDBY,CREATEDDATE,LASTMODIFIEDBY,LASTMODIFIEDDATE,APPLICATION) values (NEXTVAL('SEQ_EG_ACTION'),'WorksSpillOverLineEstimateAuthorityNames','/lineestimate/ajax-assignmentByDepartmentAndDesignation',null,(select id from EG_MODULE where name = 'WorksLineEstimate'),3,'Spillover Line Estimate Authority Names','false','egworks',0,1,now(),1,now(),(select id from eg_module  where name = 'Works Management'));
insert into eg_roleaction (roleid, actionid) values ((select id from eg_role where name = 'Super User'),(select id from eg_action where name ='WorksSpillOverLineEstimateAuthorityNames' and contextroot = 'egworks'));
insert into eg_roleaction (roleid, actionid) values ((select id from eg_role where name = 'Works Creator'),(select id from eg_action where name ='WorksSpillOverLineEstimateAuthorityNames' and contextroot = 'egworks'));

--rollback delete FROM EG_ROLEACTION WHERE roleid = (SELECT id FROM eg_role WHERE name = 'Works Creator') and actionid = (SELECT id FROM eg_action WHERE name ='WorksSpillOverLineEstimateAuthorityNames' and contextroot = 'egworks');
--rollback delete FROM EG_ROLEACTION WHERE roleid = (SELECT id FROM eg_role WHERE name = 'Super User') and actionid = (SELECT id FROM eg_action WHERE name ='WorksSpillOverLineEstimateAuthorityNames' and contextroot = 'egworks');
--rollback delete FROM EG_ACTION WHERE name = 'WorksSpillOverLineEstimateAuthorityNames' and contextroot = 'egworks';
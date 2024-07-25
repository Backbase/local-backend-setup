/*what's the catalogue of available business functions?
  */

  select bf.id ,bf.function_name,p.name 
  from access-control.applicable_function_privilege afp
  join access-control.privilege p on p.id = afp.privilege_id
  join access-control.business_function bf on bf.id = afp.business_function_id
  group by bf.id ,bf.function_name,p.name
  order by bf.id ASC


/* check the ingested custom function groups
  */

  select fg.name as"FUNCTION_GROUP" , bf.function_name as "BUSINESS_FUNCTION", p.name as "PRIVILEGE" 
  from access-control.function_group fg
  join access-control.function_group_item fgi on fgi.function_group_id = fg.id
  join access-control.applicable_function_privilege  afp on fgi.afp_id = afp.id
  join access-control.business_function bf on afp.business_function_id = bf.id
  join access-control.privilege p ON afp.privilege_id = p.id
  group by fg.name ,bf.function_name, p.name


/*check if user has SA & Job Role
  */

  select ue.USERNAME ,le.external_id as "LE_EXT_ID" ,sa.external_id as "SA_EXT_ID" 
  from access-control.legal_entity le
  join access-control.participant p on p.legal_entity_id = le.id
  join access-control.service_agreement sa on p.service_agreement_id =sa.id
  join access-control.user_context uc on uc.service_agreement_id = sa.id
  join access-control.user_assigned_function_group uafg on uafg.user_context_id = uc.id
  join access-control.function_group fg on uafg.function_group_id =fg.id
  join backbase_identity.USER_ENTITY ue on ue.ID = uc.user_id


/* check if user has SA,Job Role,data groups,arrangements
  */

  select ue.USERNAME ,le.external_id as "LE_EXT_ID" ,sa.external_id as "SA_EXT_ID",fg.name as "FUNCTION_GROUP", dg.name as "DATA_GROUP", a.name as "ARRANGEMENT_NAME" 
  from access-control.legal_entity le
  join access-control.participant p on p.legal_entity_id = le.id
  join access-control.service_agreement sa on p.service_agreement_id =sa.id
  join access-control.user_context uc on uc.service_agreement_id = sa.id
  join access-control.user_assigned_function_group uafg on uafg.user_context_id = uc.id
  join access-control.function_group fg on uafg.function_group_id =fg.id
  join access-control.backbase_identity.USER_ENTITY ue on ue.ID = uc.user_id
  join access-control.data_group dg on dg.service_agreement_id =sa.id
  join access-control.data_group_item dgi on dgi.data_group_id = dg.id
  join arrangement-manager.arrangement a on a.id = dgi.data_item_id






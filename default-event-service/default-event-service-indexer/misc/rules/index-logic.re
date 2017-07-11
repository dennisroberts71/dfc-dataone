# VERSION: 22
#
# Adapted from iplant DE rules, specific to indexing
#
# These rules will be called by the hooks implemented in default-dataone-indexer.re.  The rule names should be
# prefixed with 'ipc' and suffixed with the name of the rule hook that will call the custom rule.

@include 'index-amqp'
@include 'index-json'

COLLECTION_TYPE = 'collection'
DATA_OBJECT_TYPE = 'data-object'
RESOURCE_TYPE = 'resource'
RESOURCE_GROUP_TYPE = 'resource-group'
USER_TYPE = 'user'

  
sendMsg(*Topic, *Msg) {
  errorcode(ipc_amqpSend(*Topic, *Msg));
  0;
}   

mkUserObject(*Field, *Name, *Zone) =
  if (*Zone == '') then
    ipc_jsonObject(*Field, list(ipc_jsonString('name', *Name),
                                ipc_jsonString('zone', $rodsZoneClient)))
  else
    ipc_jsonObject(*Field, list(ipc_jsonString('name', *Name),
                                ipc_jsonString('zone', *Zone)))

mkAuthorField() = mkUserObject('author', $userNameClient, $rodsZoneClient)

mkEntityField(*UUID) = ipc_jsonString('entity', *UUID)

mkPathField(*Path) = ipc_jsonString('path', *Path)



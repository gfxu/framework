SELECT 
    A.MODULE_CODE,A.CONFIG_ITEM_CODE,B.PARAM_CODE,B.PARAM_VALUE, B.DEFAULT_PARAM_VALUE
FROM 
    CONFIG_ITEM A,CONFIG_ITEM_PARAM B
WHERE 
    A.CONFIG_ITEM_ID = B.CONFIG_ITEM_ID
##AND
##    A.MODULE_CODE IN :moduleList
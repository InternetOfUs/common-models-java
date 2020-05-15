db.createUser({
	user : 'wenetTaskManager',
	pwd : 'password',
	roles : [ {
		role : 'readWrite',
		db : 'wenetTaskManagerDB'
	} ]
})
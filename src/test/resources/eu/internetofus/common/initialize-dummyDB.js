db.createUser({
	user : 'dummy',
	pwd : 'password',
	roles : [ {
		role : 'readWrite',
		db : 'dummyDB'
	} ]
})
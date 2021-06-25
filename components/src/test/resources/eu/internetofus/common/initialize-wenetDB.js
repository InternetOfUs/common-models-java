db.createUser({
	user : 'wenet',
	pwd : 'password',
	roles : [ {
		role : 'readWrite',
		db : 'wenetDB'
	} ]
})

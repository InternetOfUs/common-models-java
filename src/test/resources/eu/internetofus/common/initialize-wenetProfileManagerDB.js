db.createUser({
	user : 'wenetProfileManager',
	pwd : 'password',
	roles : [ {
		role : 'readWrite',
		db : 'wenetProfileManagerDB'
	} ]
})

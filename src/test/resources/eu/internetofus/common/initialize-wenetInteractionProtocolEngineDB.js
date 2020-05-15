db.createUser({
	user : 'wenetInteractionProtocolEngine',
	pwd : 'password',
	roles : [ {
		role : 'readWrite',
		db : 'wenetInteractionProtocolEngineDB'
	} ]
})
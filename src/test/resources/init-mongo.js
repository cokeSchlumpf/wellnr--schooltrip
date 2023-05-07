db.createUser(
    {
        user: "app",
        pwd: "password",
        roles:[
            {
                role: "readWrite",
                db:   "app"
            }
        ]
    }
);
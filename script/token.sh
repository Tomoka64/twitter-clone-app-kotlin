#!/bin/sh

.  ./docker/.env

curl "https://www.googleapis.com/identitytoolkit/v3/relyingparty/verifyPassword?key=$FIREBASE_API_KEY" \
-H 'Content-Type: application/json' \
--data-binary '{"email":"test1@test.com","password":"password","returnSecureToken":true}'

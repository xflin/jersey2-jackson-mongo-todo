jersey2-jackson-mongo-todo
==========================

A sample project setup using Jersey 2 + Jackson 2 + MongoDB, that implements a
simple TO-DO note REST API.

+ GET /todo

    List To-Do items.

+ POST /todo

    {"title": "...", "body": "...", done: false}

    Create a TO-DO item.

+ PUT /todo/[id]

    {...}

    (Full) Update of a TO-DO item.

+ PUT /todo/[id]/patch

    {...}

    Patch (partial update) of a TO-DO item.

+ PUT /todo/[id]/patch

    { "done": true }

    Mark done (when value is true) or undone (when value is false).
  
+ PUT /todo/[id]/patch

    { "done": true, ", smsPhoneNo: "6501234567" }
     
    Mark done and send SMS message.

+ DELETE /todo/[id]

    Delete a TO-DO item.

To Run in Local System
----------------------

+ Install JDK 7+ and Apache Maven.
+ Install a local MongoDB and start with the default port#.
+ Install foreman (part of Heroku toolbelt).
+ Git clone and run it:

    git clone https://github.com/xflin/jersey2-jackson-mongo-todo.git

    cd jersey2-jackson-mongo-todo

    mvn package

    foreman start web

+ Open your browser (or cURL) to http://localhost:5000/todo

Heroku Env Variables
--------------------

    heroku config:set MONGODB_URL="..."

Optionally if you want to try SMS when "done" is marked:

    heroku config:set TWILIO_ACCOUNT_SID="..."
    heroku config:set TWILIO_AUTH_TOKEN="..."
    heroku config:set TWILIO_FROM_PHONE_NO="+1##########"


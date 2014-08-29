jersey2-jackson-mongo-todo
==========================

A sample project setup using Jersey 2 + Jackson 2 + MongoDB, that implements a
simple TO-DO note REST API.

+ GET /todo

  List To-Do items.

+ POST /todo
  {"title": "...", "body": "...", done: false}

  Create a TO-DO item.

+ PUT /todo/<id>
  {...}

  (Full) Update of a TO-DO item.

+ PUT /todo/<id>/patch
  {...}

  Patch (partial update) of a TO-DO item.

+ PUT /todo/<id>/patch
  { "done": true }

  Mark done (when value is true) or undone (when value is false).

+ DELETE /todo/<id>

  Delete a TO-DO item.


# CloudFoundry Messaging API

This module provides a flexible message queue API that can be called via a REST service or via a websocket.
- Receive messages from queue https://<host>/messaging/<queue>/receive
- Publish messages to queue https://<host>/messaging/<queue>/publish
- Open WebSocket on queue wss://<host>/messaging/<queue>/ws

## Prepare and Deploy

Go into the manifest and replace the org name d043918trial with your org name.

Create an instance of the rabbit service and bind it to the app

Then build the java war package with Maven
```
mvn clean install
```
Then push to Cloud Foundry
```
cf push
```



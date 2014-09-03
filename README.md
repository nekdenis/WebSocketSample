WebSocketSample
===============

Android sample application.

Always-alive service that recieves messages from websocket server, sends messages to it and delivers data to DB and activity.

As WebSocket-client library I've used https://github.com/tavendo/AutobahnAndroid

Also in the app are samples of usage:
- Loaders
- https://github.com/BoD/android-contentprovider-generator
- GPlS Location Client

Need to be fixed: 
- server tells about wrong user's credentials using status codes. But this codes sended to callback only in other fork of autobahn. So I should switch library to https://github.com/pepyakin/AutobahnAndroid

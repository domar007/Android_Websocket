var WebSocketServer = require('ws').Server
, wss = new WebSocketServer({port: 8080});


wss.broadcast = function(data) {
  for (var i in this.clients)
    this.clients[i].send(data);
};


wss.on('connection', function(ws) 
{
console.log('client verbunden...');
  ws.on('message', function(message) {
console.log('von Client empfangen: ' + message);
    wss.broadcast(message);
  });
});




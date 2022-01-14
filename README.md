# Network Routing
This project simulates a network of computers connecting a Client and Server
- Data will be sent from the Client, where it is broken up into packets and forwarded through the network in the shortest possible path (according to the next hop table)
- The packets are reconstructed by the Server at the other end

## Running
Programs must be run in the following order:
```
java NetworkNode -1
java Server
java NetworkNode 3
java NetworkNode 2
java NetworkNode 1
java Client
```
These must be run in separate terminal windows to simulate multiple computers

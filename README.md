Under construction

For development purposes, the blockchain is using a PUB-SUB messaging architecture involving REDIS.

Further configurables are under construction.

Deployment:

1. Start REDIS for use as a pub-sub messaging channel. Topics will be created automatically.

2. Run each node setting environment variable server.port=xxxx

3. For development purposes, we have a 

// todo
- finish implementation
- investigate use of Choords p2p routing tables algorithm for distribution of messages throughout the network. This could likely be too slow for this kind of architecture.
- investigate alternate use of distribution of messages through the network.
 
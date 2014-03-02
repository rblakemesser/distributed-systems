Server
X     Read input file
     Read standard input (?)
     Handle messages  (protected by Lamport’s mutex algorithm)
          Reserve
          Return
     Send responses
     Start a server on an IP address/port
     Maintain list of failover servers
     Send a client to a new server upon failure
     Wait (after a command)
     Close connection
     Synchronize book lists   (top priority)
     Synchronize client lists

     Data:
          Book list
          Client list
X          Server ID
X          Time to wait
X          Other server list


Client
     Read input file
     Read standard input
     Send messages
          Reserve
          Return
     Receive responses
     Connect to a server
     Wait (after a command)
     Close connection
     Print output file

Mutex object (Lamport Mutex) that will be included in each server
Direct Clock (one on each server, for timestamps for the mutex)
Connection?

Lamport Mutex-related code from a previous semester:
https://code.google.com/p/distmapreduce/source/browse/trunk/src/edu/utexas/ipc/?r=57


#ifndef INTERFACE_H_
#define INTERFACE_H_

#include "Server.h"
#include "Queue.h"

class Server;
//class Queue;

class Interface{
	long bandwidth;
	Server& peer;
	Queue queue;
};	
	
#endif /*INTERFACE_H_*/

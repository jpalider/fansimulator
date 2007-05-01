#ifndef SERVER_H_
#define SERVER_H_

#include <string>
#include <vector>

#include "Queue.h"
using namespace std;

class RoutingTable;
class Queue;
class Interface;

/**
 * Server class
 */
class Server {
	private:
		string name;
		int maxTrafficTypes;
		vector< Interface > interfaces;
		RoutingTable routing;
	public:
		
		
};
#endif /*SERVER_H_*/


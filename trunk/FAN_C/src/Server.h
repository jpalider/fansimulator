#ifndef SERVER_H_
#define SERVER_H_

#include <string>
#include <vector>


using namespace std;

#include "RoutingTable.h"
#include "Interface.h"
#include "FlowList.h"
#include "Packet.h"
#include "Monitor.h"

/**
 * Server class
 */
class Server {

	private:
		string name;
		int maxTrafficTypes;
		vector< Interface > interfaces;
		RoutingTable routing;
		FlowList flowList;
	public:
		void recieve(Packet&);
		void send();
		
};

#endif /*SERVER_H_*/


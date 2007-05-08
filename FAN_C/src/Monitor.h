#ifndef MONITOR_H_
#define MONITOR_H_
#include <vector>

#include "Common.h"
#include "EventList.h"
#include "Server.h"
#include "Randomize.h"

class Monitor {
	public:
	
		static std::vector<Server> servers;
		static Time clock;
		static EventList agenda;
		static Randomize randomize;

};
#endif /*MONITOR_H_*/

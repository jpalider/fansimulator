#ifndef EVENTLIST_H_
#define EVENTLIST_H_
#include <map>
#include "Common.h"
#include "Event.h"

class EventList{
		std::map<Time, Event*> list;
	public:
		EventList();
		Event* removeFirst();
		void schedule(Event& );
};

#endif /*EVENTLIST_H_*/

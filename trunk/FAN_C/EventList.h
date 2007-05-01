#ifndef EVENTLIST_H_
#define EVENTLIST_H_

#include "Event.h"

class EventList{
	public:
		EventList();
		Event* removeFirst();
		schedule(Event*);
};

#endif /*EVENTLIST_H_*/

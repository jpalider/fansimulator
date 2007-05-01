#ifndef EVENT_H_
#define EVENT_H_

#include "Common.h"
#include "Server.h"

/**
 * Class Event
 */
class Event{
	private;
		Time time;
		Server* place;
	public:
		Event(Time, Server*);
		void virtual run() = 0;
};

#endif /*EVENT_H_*/

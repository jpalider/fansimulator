#ifndef EVENT_H_
#define EVENT_H_

#include "Common.h"

class Server;

/**
 * Class Event
 */
class Event{
	private:
		Time time;
	protected:	
		Server& place;
	public:
		Event(Time, Server&);
		virtual ~Event();
		void virtual run() = 0;
};

#endif /*EVENT_H_*/


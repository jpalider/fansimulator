/**Changelog
 * Changed time variable to public - to be easily used when scheduling events 
 * 
*/
#ifndef EVENT_H_
#define EVENT_H_

#include "Common.h"

class Server;

/**
 * Class Event
 */
class Event{
	protected:
		Server& place;
	public:
		Time time;
		Event(Time, Server&);
		virtual ~Event();
		void virtual run() = 0;
};

#endif /*EVENT_H_*/


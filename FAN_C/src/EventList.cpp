/* Changelog:
 * Defined funtion EventList::schedule
 * 
 * 
 * 
 */

#include "EventList.h"

EventList::EventList()
{
	
}

void EventList::schedule(Event& newEvent) {
	list.insert( std::make_pair(newEvent.time,&newEvent) );
}

void EventList::removeFirst() {

}

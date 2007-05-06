#ifndef GENERATE_H_
#define GENERATE_H_

#include "Event.h"

class Generate : public Event{
	public:
		Generate(Time, Server&);
		void run();
		
};

#endif /*GENERATE_H_*/

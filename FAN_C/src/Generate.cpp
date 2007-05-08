
#include "Generate.h"
#include "Packet.h"
#include "Monitor.h"

Generate::Generate(Time t, Server& s) : Event(t, s){
	
}

void Generate::run(){
	Packet p = Packet(FlowIdentifier(Monitor::randomize.getNumber(5)), STREAM, 5);
	place.recieve(p);
}

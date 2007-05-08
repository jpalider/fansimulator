#ifndef PACKET_H_
#define PACKET_H_

#include "FlowIdentifier.h"

enum FlowType { STREAM, ELASTIC };

class Packet{
		int length;
		FlowType type;
	public:
		Packet(FlowIdentifier id, FlowType ftype, int l = 65535);
		
		FlowIdentifier flowID;
};

#endif /*PACKET_H_*/

#include "Packet.h"

Packet::Packet(FlowIdentifier id, FlowType ftype, int l)  {
	flowID = id;
	type = ftype;
	length = l;
}

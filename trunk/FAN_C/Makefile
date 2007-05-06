#
CC = g++ -Wno-deprecated -Wall
FLAG = -c
OFLAG = -o

.SUFFIXES: .cpp .o
.cpp.o:
	$(CC) $(FLAG) $<

all : fan

fan : main.o Common.o Depart.o Event.o EventList.o FlowIdentifier.o FlowList.o Generate.o \
	  Interface.o Monitor.o Packet.o Queue.o RoutingTable.o Server.o Randomize.o
	$(CC) $(OFLAG) fan main.o Common.o Depart.o Event.o EventList.o FlowIdentifier.o FlowList.o Generate.o \
	  Interface.o Monitor.o Packet.o Queue.o RoutingTable.o Server.o Randomize.o

clean :
	rm *.o 
	

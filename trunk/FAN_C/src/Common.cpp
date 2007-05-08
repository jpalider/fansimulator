#include "Common.h"	

bool TimeCompare::operator()(const Time a,const Time b){
	return a.t < b.t;
}

#include "Common.h"	

bool Time::operator< (const Time& y){
	return t < y.t;
}

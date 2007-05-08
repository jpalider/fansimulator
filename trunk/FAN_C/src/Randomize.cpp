#include <cstdlib>
#include <time.h>

#include "Randomize.h"



Randomize::Randomize(){
	srand( time(NULL) );	
}

int Randomize::getNumber(int range){
	return rand() % range;
}


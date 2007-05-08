/**Changelog
 * Removed operator< as it gave strange error about "discard qualifiers"
 * Created class TimeCompare responsible for comparing time;
 * 
 */
#ifndef COMMON_H_
#define COMMON_H_

class Time{
	public:
		double t;
};

class TimeCompare{
	public:
		bool operator()(const Time,const Time);
};

#endif /*COMMON_H_*/

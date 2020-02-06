#include<stdio.h>
#include<semaphore.h>
#include<stdlib.h>
#include<string.h>
#include<unistd.h>
#include<netinet/in.h>
#include<iostream>
#include<netdb.h>
#include<chrono>
#include<ctime>
#include<pthread.h>
using namespace std;
string getOsName();
struct BEACON{
	int ID;
	int startUpTime;
	int timeInterval;
	char IP[4];
	int cmdPort;
};
int main(int argc, char const *argv[]){
	while(1){
	}
	struct sockaddr_in sin;
	struct hostent *host = gethostbyname(argv[1]);
	unsigned int svrAddr = *(unsigned long *) host->h_addr_list[0];
	unsigned short svrPort = atoi(argv[2]);
	memset (&sin, 0, sizeof(sin));
	sin.sin_family = AF_INET;
	sin.sin_addr.s_addr =svrAddr;
	sin.sin_port = htons(svrPort);
	return 0;
}
void BeaconSender(){
}
void getLocalOS(char OS[16], int *valid){
	string OS_name = getOsName();
	if(OS_name.compare("Error"))
		*valid = 0;
	else 
		*valid = 1;
	strcpy(OS,OS_name.c_str());
}

void GetLocalTime(int *time, int *valid){
	auto current_time = chrono::system_clock::now();
}

string getOsName()
{
    #ifdef _WIN32
    return "Windows 32-bit";
    #elif _WIN64
    return "Windows 64-bit";
    #elif __APPLE__ || __MACH__
    return "Mac OSX";
    #elif __linux__
    return "Linux";
    #elif __FreeBSD__
    return "FreeBSD";
    #elif __unix || __unix__
    return "Unix";
    #else
    return "Error";
    #endif
}

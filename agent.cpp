#include <stdio.h>
#include <math.h>
#include <time.h>
#include <string>
#include <string.h>
#include <iostream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <stdlib.h>

#define PORT	30000 
#define MAXLINE 1020 
int last_time = 0;
typedef struct BEACON
 {
 	int 	ID;                     // randomly generated during startup
 	int 	startUpTime; // the time when the client starts
	int     timeInterval; // the time period that this beacon will be repeated
 	char  	IP[4];	            // the IP address of this client
 	int		cmdPort;       // the client listens to this port for manager commands
 }BEACON_t;
char *BEACtoChar(BEACON_t toConvert);
char *toBytes(int toConvert);
int main() {
	int server_socket;
	struct sockaddr_in sin;
	memset(&sin, 0, sizeof(sin));
	sin.sin_family = AF_INET; // or AF_INET6 (address family)
	sin.sin_port = htons(30000);
	sin.sin_addr.s_addr= INADDR_ANY;
	BEACON_t tosend;
	tosend.ID = 65;
	tosend.startUpTime = 66;
	tosend.timeInterval = 67;
	char *toprint = BEACtoChar(tosend);
	std::cout << "printing?" << std::endl;
	if((server_socket = socket(AF_INET,SOCK_DGRAM,0))< 0)
	{
		printf("bind error\n");
	}
	sendto(server_socket,toprint,20*sizeof(char),
			MSG_CONFIRM, (const struct sockaddr *) &sin,  
			sizeof(sin)); 	
} 
// To send ID | startUpTime | timeInterval | IP | cmdPort
char *BEACtoChar(BEACON_t toConvert){
	char *toreturn = (char *)malloc(20*sizeof(char));
	char *cmdP = toBytes(toConvert.cmdPort);
	char *timeInterval = toBytes(toConvert.timeInterval);
	char *startUpTime = toBytes(toConvert.startUpTime);
	char *ID = toBytes(toConvert.ID);
	for (int i = 0; i < 4; ++i) {
		toreturn[i] = cmdP[i];
	}
	for (int i = 0; i < 4; ++i) {
		toreturn[4+i] = toConvert.IP[i];
	}
	for (int i = 0; i < 4; ++i) {
		toreturn[8+i] = timeInterval[i];
	}
	for (int i = 0; i < 4; ++i) {
		toreturn[12+i] = startUpTime[i];
	}
	for (int i = 0; i < 4; ++i) {
		toreturn[16+i] = ID[i];
	}
	return toreturn;
}

char *toBytes(int toConvert){
	bool holder[32];
	char *toreturn = (char *)malloc(4*sizeof(char));
	for (int i = 32; i >= 0; --i) {
		if(toConvert >= pow(2,i)){
			holder[i] = true;
			toConvert -= pow(2,i);
		}
		else{
			holder[i] = false;
		}
	}
	for (int i = 0; i < 4; ++i) {
		char tostore = 0;
		for (int k = 0; k < 8; ++k) {
			if(holder[i*8+k] == true){
				tostore += pow(2,k);
			}
		}
		toreturn[i] = tostore;
	}
	return toreturn;
}

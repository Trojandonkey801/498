#include <fstream>
#include <stdio.h>
#include <ctime>
#include <chrono>
#include <math.h>
#include <ratio>
#include <time.h>
#include <unistd.h>
#include <string>
#include <string.h>
#include <string.h>
#include <iostream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <sys/socket.h>

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
void func(int sockfd);
int receiveFully(int client_socket, char *buffer, int length);
char *BEACtoChar(BEACON_t toConvert);
char *toBytes(int toConvert);
/**
int main() {
	int server_socket;
	struct sockaddr_in sin;
	memset(&sin, 0, sizeof(sin));
	sin.sin_family = AF_INET; // or AF_INET6 (address family)
	sin.sin_port = htons(30000);
	sin.sin_addr.s_addr= INADDR_ANY;
	BEACON_t tosend;
	tosend.ID = rand()%10000;
	tosend.startUpTime = 66;
	tosend.timeInterval = 67;
	char *toprint = BEACtoChar(tosend);
	std::cout << "printing?" << std::endl;
	if((server_socket = socket(AF_INET,SOCK_DGRAM,0))< 0)
	{
		printf("bind error\n");
	}
	sendto(server_socket,toprint,20*sizeof(char),
			0, (const struct sockaddr *) &sin,  
			sizeof(sin)); 	
} 
*/
int main(int argc, char *argv[])
{
	
	return 0;
}

int get_Time(){
	auto start = std::chrono::system_clock::now();
	auto now_ms = std::chrono::time_point_cast<std::chrono::milliseconds>(start);
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

void connectTCP(){
   	int server_socket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
 	struct sockaddr_in sin;
    memset(&sin, 0, sizeof(sin));
    //sin.sin_len = sizeof(sin);  // comment this line out if running on pyrite (linux) 
    sin.sin_family = AF_INET; // or AF_INET6 (address family)
    sin.sin_port = htons(1234);
    sin.sin_addr.s_addr= INADDR_ANY;
    if (bind(server_socket, (struct sockaddr *)&sin, sizeof(sin)) < 0) 
    {
        // Handle the error.
		printf("bind error\n");
    }
	func(server_socket);
}
int MAX = 2096;
void func(int sockfd)
{
	char buff[MAX];
    int n;
    for (;;) {
        n = 0;
		std::string towrite = "writing";
		strcpy(buff,towrite.c_str());
        write(sockfd, buff, sizeof(buff));
        bzero(buff, sizeof(buff));
        read(sockfd, buff, sizeof(buff));
        printf("From Server : %s", buff);
        if ((strncmp(buff, "exit", 4)) == 0) {
            printf("Client Exit...\n");
            break;
        }
    }
}

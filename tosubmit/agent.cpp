#include <fstream>
#include <thread>
#include <sys/utsname.h>
#include <stdio.h>
#include <ctime>
#include <time.h>
#include <chrono>
#include <math.h>
#include <ratio>
#include <time.h>
#include <unistd.h>
#include <string>
#include <string.h>
#include <iostream>
#include <stdio.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <sys/socket.h>

#define PORT	30000 
#define MAXLINE 1020 
int last_time = 0;
/**
 * This is a struct containing the information to send in a beacon
 *
 */
typedef struct BEACON
 {
 	int 	ID;                     // randomly generated during startup
 	int 	startUpTime; // the time when the client starts
	int     timeInterval; // the time period that this beacon will be repeated
 	char  	IP[4];	            // the IP address of this client
 	int		cmdPort;       // the client listens to this port for manager commands
 }BEACON_t;


// function and variable declarations
void func(int sockfd);
int receiveFully(int client_socket, char *buffer, int length);
char *BEACtoChar(BEACON_t toConvert);
char *toBytes(int toConvert);
char *opp_toBytes(int toConvert);
void connectTCP();
int currentTime();
void spectoBytes(int i,char * result);
BEACON tosend;

/**
 * This functiono sends a UDP based beacon to the server
 * on port 30000
 * @param BEACON to send
 */
void send_Beacon(BEACON totransfer){
	int server_socket;
	struct sockaddr_in sin;
	memset(&sin, 0, sizeof(sin));
	sin.sin_family = AF_INET; // or AF_INET6 (address family)
	sin.sin_port = htons(30000);
	sin.sin_addr.s_addr= INADDR_ANY;
	char *toprint = BEACtoChar(totransfer);
	if((server_socket = socket(AF_INET,SOCK_DGRAM,0))< 0)
	{
		printf("bind error\n");
	}
	sendto(server_socket,toprint,20*sizeof(char),
			0, (const struct sockaddr *) &sin,  
			sizeof(sin)); 	
} 

/**
 * Initializes the beacon that this agent operates with
 */
void init_to_Send(){
	tosend.ID = rand()%10000;
	srand((unsigned) time(0));
	tosend.cmdPort = rand()%100+10000;
	tosend.IP[0] = *toBytes(127);
	tosend.IP[1] = *toBytes(0);
	tosend.IP[2] = *toBytes(0);
	tosend.IP[3] = *toBytes(1);
	//tosend.startUpTime = currentTime();
	tosend.startUpTime = 8642;
	tosend.timeInterval = 60;//Time in interval
}

/**
 * Is responsible for sending a new beacon at regular intervals.
 * The time is specified such that it sleeps for timeInterval seconds
 * by calling usleep
 */
void time_Beacon(){
	for (int i = 0; i < 200; ++i) {
		std::cout << "sending" << std::endl;
		send_Beacon(tosend);
		usleep((tosend.timeInterval-1)*1000000);
	}
}

/*
 * The main method that initializes, and starts the threads that the 
 * agent uses
 *
 */
int main(int argc, char *argv[])
{
	init_to_Send();
	std::thread t1(time_Beacon);
	usleep(100);
	std::thread t2(connectTCP);
	t1.join();
	t2.join();
	return 0;
}


//--------------------------------------------------
//Useful methods
//--------------------------------------------------


/**
 * To send ID | startUpTime | timeInterval | IP | cmdPort
 * This function converts a beacon into that char pointer
 * to be sent over UDP
 * It is encoded in the above way, by specifying a central
 * char pointer that is sent
 */
char *BEACtoChar(BEACON_t toConvert){
	char *toreturn = (char *)malloc(20*sizeof(char));
	char *cmdP = toBytes(toConvert.cmdPort);
	char *IP = toConvert.IP;
	char *timeInterval = toBytes(toConvert.timeInterval);
	char *startUpTime = toBytes(toConvert.startUpTime);
	//spectoBytes(toConvert.startUpTime,startUpTime);
	char *ID = toBytes(toConvert.ID);
	for (int i = 0; i < 4; ++i) {
		toreturn[i] = cmdP[i];
	}
	for (int i = 0; i < 4; ++i) {
		toreturn[4+i] = *toBytes(toConvert.IP[i]);
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

/**
 * Converts the given bytes to an integer
 * Pulled from sample codes on canvas
 */
int toInteger32(char *bytes)
{
	int tmp = (bytes[3] << 24) + 
	          (bytes[2] << 16) + 
	          (bytes[1] << 8) + 
	          bytes[0];
	return tmp;
}
/**
 * A method to convert a given int to Bytes
 * it goes through and checks individual bits
 * This function however reverses the bits.. 
 * It's not actually used anywhere
 */
char *opp_toBytes(int toConvert){
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
	bool temp[32];
	for (int i = 0; i < 32; ++i) {
		temp[i] = holder[32-i];
	}
	for (int i = 0; i < 4; ++i) {
		char tostore = 0;
		for (int k = 0; k < 8; ++k) {
			if(temp[i*8+k] == true){
				tostore += pow(2,k);
			}
		}
		toreturn[i] = tostore;
	}
	return toreturn;
}

/**
 * A method to convert a given int to Bytes
 * it goes through and checks individual bits
 */
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

/**
 * Returns current time in epochs in miliseconds 
 *
 */
int currentTime(){
	using namespace std::chrono;
	seconds s = duration_cast<seconds>(
			system_clock::now().time_since_epoch()
			);
	int time = s.count();
	return time;
}

/**
 * Calls the CurrentTime() function get return a pointer to current time
 */
void GetLocalTime(int *time, int *valid){
	*time = currentTime();
}

/**
 * Helper function to return what kind of OS is running
 * This function simply checks if each OS is defined in filepath
 * This is a required function according to the homework specs
 */
void getLocalOS(char OS[16],int *valid){
#if defined(_WIN32)
	strcpy(OS,"OS is: swindows");
#elif defined(__linux__)
	strcpy(OS,"OS is: linux");
#elif defined(__APPLE__)
	strcpy(OS,"OS is: apple");
#elif defined(BSD)
	strcpy(OS,"OS is: BSD");
#elif defined(__QNX__)
	strcpy(OS,"OS is: QNX");
#else
	strcpy(OS,"null");
#endif
}

//--------------------------------------------------
//TCP STUFF
//--------------------------------------------------
void connectTCP(){
	usleep(300000);
	int sock = 0, valread; 
	struct sockaddr_in serv_addr; 
	char buffer[1024] = {0};
	if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0) 
	{ 
		printf("\n Socket creation error \n"); 
	} 

	serv_addr.sin_family = AF_INET; 
	serv_addr.sin_port = htons(tosend.cmdPort); 
	serv_addr.sin_addr.s_addr= INADDR_ANY;
	if (connect(sock, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0) 
	{ 
		printf("\nConnection Failed \n"); 
	} 
	std::string temp;
	valread = read(sock ,buffer, 1024); 
	printf("%s\n",buffer ); 
	std::string s(buffer);
	temp = s;
	int valid;
	char OS[16];
	int *time_tt = (int *)malloc(sizeof(int));
	if(temp.find("OS") != std::string::npos){
		getLocalOS(OS,&valid);
		send(sock,OS,sizeof(OS),0);
	}
	if(temp.find("Time") != std::string::npos){
		time_t my_time = time(NULL);
		char temp_int[50];
		sprintf(temp_int,"%s", ctime(&my_time));
		GetLocalTime(time_tt,&valid);
		std::string timeis = "Time on machine ";
		char tosend[50];
		strcpy(tosend,timeis.c_str());
		strcat(tosend,temp_int);
		send(sock,tosend,sizeof(tosend),0);
	}
}

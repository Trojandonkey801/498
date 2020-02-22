#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <unistd.h>
typedef struct {
	char OS[16];
	char valid;
}GET_LOCAL_OS;

typedef struct {
	int time;
	char valid;
}GET_LOCAL_TIME;

void GetLocalOS(GET_LOCAL_OS *ds){
	char *towrite;
#if defined(_WIN32)
	towrite = "Windows";
#elif defined(__linux__)
	towrite = "Linux";
#elif defined(__APPLE__)
	towrite = "Apple";
#elif defined(BSD)
	towrite = "BSD";
#elif defined(__QNX__)
	towrite = "QNX";
#else
	towrite = "Erro";
#endif
	strcpy(ds->OS,towrite);
}

void GetLocalTime(GET_LOCAL_TIME *ds){
	time_t rawtime;
	time ( &rawtime );
	ds->time = rawtime;
}

int getValue(char buf[]){
	return (buf[3] << 24) +
		(buf[2] << 16) +
		(buf[1] << 8) +
		(buf[0]) ;
}

void setValue(char buf[], int val){
	buf[3] = (char)(val >> 24);
	buf[2] = (char)(val >> 16);
	buf[1] = (char)(val >> 8);
	buf[0] = (char)(val);
}

void printBinaryArray(char *buffer, int length)
{
    int i=0;
    while (i<length)
    {
		printf("%d ", buffer[i]);
		i++;
	}
	printf("\n");
}

void handleHeader(char* buf,int *length, int* valid){
	int i = 0;
	char temp_store[100];
	for (i = 0; i < 100 && buf[i] != 0; ++i) {
		temp_store[i] = buf[i];
	}
	temp_store[i+1] = '\0';
	printf("%s",temp_store);
	if(strcmp(temp_store,"GetLocalTime") == 0)
		*valid = 1;
	char temp[4];
	int offset = 100;
	for (int i = offset; i < offset+4; ++i) {
		temp[i-offset] = buf[i];
	}
	*length = getValue(temp);
}

void handleTimePayload(char *buffer,int length, char* valid){ 
	char temp_int[4];
	for (int i = 0; i < 4; ++i) {
		temp_int[i] = buffer[i];
	}
	int value = getValue(temp_int);
	char temp_char[length-4];
	for (int i = 4; i < length; ++i) {
		temp_char[i-4] = buffer[i];
		printf("%c",temp_char[i-4]);
	}
}

void listenTCP(){
	char buffer[124] = {0};
	int server_socket = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);
	printf("server_socket = %d\n", server_socket);

	// bind to a port
	struct sockaddr_in sin;
	memset(&sin, 0, sizeof(sin));
	//sin.sin_len = sizeof(sin);  // comment this line out if running on pyrite (linux) 
	sin.sin_family = AF_INET; // or AF_INET6 (address family)
	sin.sin_port = htons(1234);
	sin.sin_addr.s_addr= INADDR_ANY;

	if (bind(server_socket, (struct sockaddr *)&sin, sizeof(sin)) < 0) 
	{
		printf("bind error\n");
	}

	listen(server_socket, 5); /* maximum 5 connections will be queued */
	int counter = 0;
	while (1)
	{
		struct sockaddr client_addr;
		unsigned int client_len;
		printf("accepting ....\n");
		int client_socket = accept(server_socket, &client_addr, &client_len);
		printf("request %d comes ...\n", counter++);
		read(client_socket,buffer,104);
		char header[104];
		for (int i = 0; i < 104; ++i) {
			header[i] = buffer[i];
		}
		int valid;
		int length;
		handleHeader(header,&length,&valid);
		char *payload = malloc(length * sizeof(char));
		read(client_socket,buffer,length);
		handlePayload()
		for (int i = 0; i < length; ++i) {
		}
		send(client_socket, 124, 0); // 4 bytes first
	}
}


int main(int argc, char *argv[])
{
	listenTCP();
	return 0;
}


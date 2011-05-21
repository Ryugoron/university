#include <stdio.h>
#include <stdlib.h>

int main(int argc, char * argv[]){
	int i=0;
	int n=100;
	
	if(argc >1){
		n = atoi(argv[1]);
	}

	char buffer[4];

	//Hello World abfangen
	scanf("%s\n",buffer);
	scanf("%s\n",buffer);
	while(i<=n){
		scanf("%s\n",buffer);
		if(strncmp(buffer,"154",3)){
			printf("%d : %s\n",i,buffer);
		}
		i++;
	}
}

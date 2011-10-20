#include <stdio.h>
#include <stdlib.h>

int main(int argc, char *argv[]){
	int n = 100;
	int i;
	
	if(argc > 1){
		n = atoi(argv[1]);		
	}
	for(i = 0; i<=n; i++){
		printf("%d\n",i);
	}
}

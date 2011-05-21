/*
 Kompilieren mit gcc -Wa,--execstack test.c
*/
#include <stdlib.h>
#include <string.h>
#include <stdio.h>


int f(int a, int b);
int g();

int main(){
  int func_size;

  int (*func_pointer)(int, int);
  char *function_dump;	
	
  func_size = &g - &f;
  function_dump = (void *)malloc(func_size);
  memcpy(function_dump, &f, func_size);

  printf ("Address of function f: %p \n",&f);
  printf ("Length of function f:  %d \n",func_size);
  
  int i;
  printf ("Aus dem Heap:\n");
  for (i = 0; i < func_size; ++i){
   printf ("Adresse %p \t %#x \n",function_dump+i,* ((unsigned char *)(function_dump+i)));
  }
  printf("Aufrufen:\n");
  func_pointer = (int (*)(int, int))function_dump;
  int ergebnis = func_pointer(2,4);
  printf("Ergebnis: %d\n",ergebnis);

  return 0;  
}

int f(int a, int b){
  return a+b;
}

int g() {
  return 0;
}

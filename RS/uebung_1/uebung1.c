/*
Rechnersicherheit
Übung 1
Oliver Wiese  VON WEGEN MUAHAHAHHAHAHAHAH
*/

/*
Idee:
(1) Zeiger auf Helpfunktion
(2) ggf. Bestimmung der Länge der Funktion
(3) Auslesen des Speicherbereichs
(4) Ausgabe als Hex im Terminal
(5) Funktion im Speicher kopieren und ausführen


Probleme:
Execution aus dem Heap herraus
*/
#include <stdio.h>
#include <stdlib.h>


int helpfunction();
int helpfunction2();

int main(){
  int a;
  unsigned char *pointer1;
  unsigned char *pointer2;
	
  int func_size;

  void (*func_pointer)();
  char *function_dump;	

  pointer1 = &helpfunction;
  pointer2 = &helpfunction2;

  func_size = pointer1 - pointer2;

  function_dump = (void *)malloc(func_size*sizeof(helpfunction2));
  
  memcpy(function_dump, pointer2, func_size*sizeof(helpfunction2));

  printf ("Pointer of helpfunction: %p \n",pointer1);
  printf ("helpfuc 2: %p \n",pointer2);
  printf("dif. %d \n",(pointer1 - pointer2));
   
  for (a = 0; a < func_size; a++){
   printf ("Adresse %p \t %#x \n",pointer2+a,*(pointer2+a));
  }
 // mprotect(2)
 // -fno-stack-protector -z execstack ? 

  func_pointer = function_dump;

  //Funktion ausführen
  func_pointer();
  
  

  

  
  return 0;  
}

int helpfunction2(){
  printf("Helpfunction2");
  return 1;
}


int helpfunction (){
  int erg;
  erg = 1+1;
  return erg;
}






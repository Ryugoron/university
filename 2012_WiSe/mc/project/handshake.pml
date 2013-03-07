#include "handshake.h"
/* Paper states array start index at 1, we take 0 instead */

byte incs = 0;

init
{
  /* Initial values of 'havePrivilege' is 1 for pid=1, 0 otherwise */
  havePrivilege[0] = 1;

  byte i = 0;

  for (i: 0 .. (N-1)) {
    run Process(i);
  }
}

proctype Process(byte i)
{
  int _c = 0, __c = 0;
  int j,n;
  do
    :: true -> do
                :: mailbox[i] ?? REQUEST,j,n -> p2(i,j,n);
                :: else -> break;
               od;
               if
                :: true -> skip; /*  chillout */
                :: true -> p1(i,_c,__c,j,n); /* process i wants to enter cs */
               fi
  od
}

//ltl claim1 { [] (incs <= 1)}

/*
b) eventually every reliable process has the same value in its local variable
  <> (finalValues[0] == finalValues[1] && finalValues[1] == finalValues[2] && .... && finalValues[N-2] == finalValues[N-1])
  if all reliable processes have the same initial value,  then their final value is the same as their common initial value
  [] (initialValue[0]...initialValue[N-1]=:a => finalValue[0]...finalValue[N-1] = a)
*/

#include "byzantine.h"

init
{
  // Initial values for all processes
  byte i;
  i = 0;
  do :: (i < M) -> initialValues[i] = INITVALUE; finalValues[i] = i; i++;
     :: else -> break;
  od;
  // Spawn processes
  i = 0;
  do :: (i < N) -> run Reliable(i); i++; // N reliable processes
     :: ((i >= N) && (i < M)) -> run Unreliable(i); i++; // K unreliable processes
     :: else -> break;
  od
}


proctype Unreliable (byte processId)
{
  byte round;
  int barrier = M;
  round = 0;
  wait(barrier); 
  do :: (round < K+1) -> wait(barrier);
                         wait(barrier);
     :: else -> break;
  od;
}

proctype Reliable (byte processId)
{
  byte round;
  byte msgReceived[M];
  byte majority;
  byte msgCounter[2];
  byte msg;
  byte _c;
  byte localVar;
  int barrier = M;
  
  round = 0;
  barrier = M;
  localVar = initialValues[processId];
  wait(barrier); 
  do :: (round >= K+1) -> break;
     :: (round < K+1)  -> // phase 1: broadcast localVar value
                broadcast(_c, localVar, processId);
                wait(barrier); // implement barrier that makes sense
                // reset counters
                msgCounter[0] = 0;
                msgCounter[1] = 0;
                // receive sent values, assumption: only 0s and 1s are sent
                _c = 0;
                do
                :: (_c < M) -> if 
                               :: (empty(A[_c].ch[processId])) -> skip; // maybe nothing was sent
                               :: (nempty(A[_c].ch[processId])) -> A[_c].ch[processId] ? msg;
                                                                   msgCounter[msg]++; 
                               fi; _c++;
                :: (_c >= M) -> break;
                od;
                skip;
                // compute majority
                d_step {
                  if :: (msgCounter[0] > M/2) -> majority = 0;
                     :: else -> majority = 1;
                  fi;
                };
                // phase 2: in round i the i-th process sends the majority value 
                // it received in the first phase
                if :: (processId == round) -> broadcast(_c, majority, processId);
                   :: else -> skip;
                fi;
                wait(barrier);
                // if i have recevied at least N time same value, set my localVar value to this
                // otherwise to the value the i-th process sent as majority
                if :: (msgCounter[1] >= N) -> // at least N ones
                                              localVar = 1; 
                   :: (msgCounter[0] >= N) -> // at least n zeros
                                              localVar = 0;
                   :: else -> if :: (empty(A[round].ch[processId])) -> skip;
                                 :: A[round].ch[processId]?localVar;
                              fi;
                fi;
                // empty channel if msg wasnt used
                if :: (empty(A[round].ch[processId])) -> skip;
                   :: A[round].ch[processId]?_;
                fi;
                // end empty
                round++;
  od;
  // protocol finished after k+1 rounds
  finalValues[processId] = localVar;
}

//ltl claim1 { <> (finalValues[0] == finalValues[1])}

/*
b) eventually every reliable process has the same value in its local variable
  <> (finalValues[0] == finalValues[1] && finalValues[1] == finalValues[2] && .... && finalValues[N-2] == finalValues[N-1])
  if all reliable processes have the same initial value,  then their final value is the same as their common initial value
  [] (initialValue[0]...initialValue[N-1]=:a => finalValue[0]...finalValue[N-1] = a)
*/

#include "byzantine.h"

bit initial =0;

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
  od;
  initial = 1;
}


proctype Unreliable (byte processId)
{
  byte round;
  int barrier = M;
  round = 0;
  byte locC;
  wait(barrier); 
  do :: (round < K+1) -> 
        wait(barrier);
        locC = 0;
        // Phase 1 : Send abitrary value or nothing
        if
            :: (1)  ->  skip;
            :: (1)  ->  broadcast(locC, 0 , processId);
            :: (1)  ->  broadcast(locC, 1 , processId);
        fi;
        wait(barrier);
        
        locC = 0;        
        do
            :: (locC < M) -> if :: (empty(A[locC].ch[processId])) -> skip;
                             :: (nempty(A[locC].ch[processId])) -> 
                                A[locC].ch[processId]?_; // throw away
                          fi;locC++;
            :: else -> break;
        od;
        // Phase 2 : Nothing, except we have to send in phase i = processId
        locC = 0;
        wait(barrier);        
        if
            :: processId != round   -> if :: (empty(A[round].ch[processId])) -> skip;
                                          :: (nempty(A[round].ch[processId])) -> A[round].ch[processId]?_;
                                       fi;
            :: else -> 
                if
                    :: (1)  -> broadcast(locC, 0 , processId);
                    :: (1)  -> broadcast(locC, 1 , processId);
                fi;
        fi;
        locC = 0;
        round++;
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
  byte locC;
  byte localVar;
  int barrier = M;
  
  round = 0;
  barrier = M;
  localVar = initialValues[processId];
  wait(barrier); 
  do :: (round >= K+1) -> break;
     :: (round < K+1)  -> // phase 1: broadcast localVar value
                broadcast(locC, localVar, processId);
                wait(barrier); // implement barrier that makes sense
                // reset counters
                msgCounter[0] = 0;
                msgCounter[1] = 0;
                // receive sent values, assumption: only 0s and 1s are sent
                locC = 0;
                do
                :: (locC < M) -> if 
                               :: (empty(A[locC].ch[processId])) -> skip; // maybe nothing was sent
                               :: (nempty(A[locC].ch[processId])) -> A[locC].ch[processId] ? msg;
                                                                   msgCounter[msg]++; 
                               fi; locC++;
                :: else -> break;
                od;
                skip;
                // compute majority
                d_step {
                  if :: (msgCounter[0] > M/2) -> majority = 0;
                     :: else -> majority = 1;
                  fi;
                };
                wait(barrier);                
                // phase 2: in round i the i-th process sends the majority value 
                // it received in the first phase
                if :: (processId == round) -> broadcast(locC, majority, processId);
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
                                 :: (nempty(A[round].ch[processId])) -> A[round].ch[processId]?localVar;
                              fi;
                fi;
                // empty channel if msg wasnt used
                if :: (empty(A[round].ch[processId])) -> skip;
                   :: (nempty(A[round].ch[processId])) -> A[round].ch[processId]?_;
                fi;
                // end empty
                round++;
                wait(barrier);
  od;
  // protocol finished after k+1 rounds
  finalValues[processId] = localVar;
}

// ltl claim1 {(<> ((initial == 1) && finalValues[0] == finalValues[1]))}

// ltl claim2 {(((<>(initial == 1)) && initialValues[0] == initialValues[1]) -> <>(finalValues[0] == finalValues[1] && finalValues[0] == initialValues[0]))}

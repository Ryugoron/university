#include "byzantine.h"

// auxillary variable to indicate that the initialisation of variables is done
bit initial = 0;

init
{
  // Initial values for all processes
  byte i;
  i = 0;
  byte value = INITVALUE;
  do
    // uncomment the first condition to let all the processes have the same initial value
    // the second condition is used for alternating initial values
    // :: (i < M) -> initialValues[i] = INITVALUE; finalValues[i] = i; i++;         // For all initvalues are equal 
     :: (i < M)     -> initialValues[i] = value; value = 1 - value; finalValues[i] = i; i++;
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
        atomic {
          locC = 0;
          do :: (locC < M) -> A[processId].ch[locC]!0; locC++;
             // :: (locC < M) -> skip;locC++;
             :: (locC < M) -> A[processId].ch[locC]!1; locC++;
             :: (locC >= M) -> break;
          od;
        }
        wait(barrier);
        
        locC = 0;        
        do
            :: (locC < M) -> if :: (empty(A[locC].ch[processId])) -> skip;
                             :: (nempty(A[locC].ch[processId])) -> 
                                A[locC].ch[processId]?_; // throw away received message
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
                atomic {
                  locC = 0;
                  do :: (locC < M) -> A[processId].ch[locC]!0; locC++;
                     :: (locC < M) -> A[processId].ch[locC]!1; locC++;
                     :: (locC >= M) -> break;
                  od;
                };
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
                wait(barrier); // wait for all to finish their broadcast
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
                if :: (msgCounter[0]*2 > M) -> majority = 0;
                   :: else -> majority = 1;
                fi;
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

/*
b)
claim1) eventually every reliable process has the same value in its local variable
 
claim2)  if all reliable processes have the same initial value,  then their final value is the same as their common initial value
*/

//      Claim1 for N = 3 
//ltl claim1 {(<> ((initial == 1) && finalValues[0] == finalValues[1] && finalValues[1] == finalValues[2]))}

//      Claim1 for N = 4
//ltl claim1 {(<> ((initial == 1) && finalValues[0] == finalValues[1] && finalValues[1] == finalValues[2]) && finalValues[2] == finalValues[3])}

//      Claim1 for N = 2 and K = 1 (error expected)
ltl claim1 {(<> ((initial == 1) && finalValues[0] == finalValues[1] ))}

//      Claim2 for N = 3 and K = 1
//ltl claim2{(<> ((initial == 1) && initialValues[0] == initialValues[1] && initialValues[1] == initialValues[2])) -> (<> ((initial == 1) && finalValues[0] == finalValues[1] && finalValues[1] == finalValues[2] && initialValues[0] == finalValues[0]))}

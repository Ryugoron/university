#include "handshake.h"
/* Paper states array start index at 1, we take 0 instead */

byte incs = 0;
int p[N];

init
{
  /* Initial values of 'havePrivilege' is 1 for pid=1, 0 otherwise */
  havePrivilege[0] = 1;
  
  /* Start N processes */
  byte i = 0;

  for (i: 0 .. (N-1)) {
    p[i] = run Process(i);
  }
}

proctype Process(byte id)
{
  // local variables for storing message infos and stuff
  local int _c = 0, __c = 0;
  local int j,n;

  /* following loop structure:
   * forever do: (1) work all request msgs
   *             (2) then either:
   *             (2a) chill
   *             (2b) try to enter CS
   * end do
   */
  do
    :: true -> /* work on all reqeust msgs, if any */ printf("Proc %d works Requests\n",id);
               do
                :: true -> skip; break;
               od unless {mailbox[id] ?? REQUEST,j,n,dummyQ,dummyA -> p2(id,j,n)};

               /* either chill of try to enter cs */
               if
                :: true -> printf("Proc %d chills\n",id);skip; /*  chillout */

progressCS:     :: true -> /* process i wants to enter cs */
                           /* p1 code begin */
                          printf("Proc %d wants to enter CS\n",id);
                          request : requesting[id] = 1;
                          if
                           :: (havePrivilege[id] == 0) ->
                                      RN[id].a[id] = RN[id].a[id] + 1;
                                      broadcast(_c,REQUEST,id,RN[id].a[id],id);
                                      /* while waiting on PRIVILEGE, work pending REQUEST msgs */
                                      do :: mailbox[id] ?? PRIVILEGE,_,_,Q[id],LN[id] -> break;
                                         :: mailbox[id] ?? REQUEST,j,n,dummyQ,dummyA -> p2(id,j,n);
                                      od;
                                      havePrivilege[id] = 1;
                           :: else -> skip;
                          fi;

                          /* CS */
                          incs++;
                          printf("Proc %d in CS\n",id);
                          cs : skip; //assert(incs <= 1); 
                          incs--;
                          /* CS End */

                          LN[id].a[id] = RN[id].a[id];
                          _c = 0;
                          do
                           :: (_c < N) -> if :: (_c == id) -> skip;
                                             :: else       ->
                                                  qElem(__c,Q[id],_c); // __c = Queue contains _c (from 1..N)?
                                                  if
                                                   :: (__c == 0 && RN[id].a[_c] == LN[id].a[_c]+1) -> qAppend(Q[id],_c);
                                                   :: else -> skip;
                                                  fi;
                                          fi;
                                          _c++;
                           :: else     -> break;
                          od;
                          qEmpty(_c,Q[id]); /* _c = empty(Queue) */
                          if
                            :: _c   -> skip; /* if Queue empty */
                            :: else -> d_step { /* if not Queue empty */
                                        havePrivilege[id] = false;
                                        // send PRIVILEGE(tail(Q),LN) to node head(Q):
                                        qPoll(_c,Q[id]);
                                        mailbox[_c] ! PRIVILEGE,0,0,Q[id],LN[id];
                                       };
                          fi;
                          requesting[id] = 0;

                          /* p2 code end */
               fi
  od
}

//ltl claim1 { [] (incs <= 1)}

// Starvation for 3 Processes
//ltl claim2 { [] ((Process[p[0]]@request || Process[p[1]]@request || Process[p[2]]@request) -> <> (incs == 1))}

// Fairness for 2 or more processes
//ltl claim3 {[]( Process[p[0]]@request -> <> (Process[p[0]]@cs))}
//ltl claim4 {[]( Process[p[0]]@request -> <> (Process[p[1]]@cs))}

// No Unnesseccary Delay for 3 Processes
//ltl claim5 {<> (([] (incs == 0 && (Process[p[0]]@request) && !(Process[p[1]]@request) && !(Process[p[2]]@request))) -> ([](<>(Process[p[0]]@cs))))}

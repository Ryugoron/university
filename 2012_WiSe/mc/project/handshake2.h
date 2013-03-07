#define N 3
#define M 9 /* set this to N*N by hand*/
#define L 5
#define LONGCOND RN[id].a[j] == LN[id].a[j]+1

/* Queue associated definitions BEGIN */
    typedef Queue { // Standard queue without overflow protection
      int q[M];
      short head = 0;
      short length = 0;
    }
    
    /* result = empty(queue), result in {0,1} */
    inline qEmpty(result,queue)
    {
      result = (queue.length == 0);
    }
    
    /* queue = queue.append(elem) */
    inline qAppend(queue,elem)
    {
      queue.q[(queue.head + queue.length) % N] = elem;
      queue.length++;
    }

    /* result= queue.poll */
    inline qPoll(result,queue)
    {
      result = queue.q[queue.head];
      queue.head = (queue.head + 1) % N;
      queue.length--;
    }
    
    /* result = queue contains elem?*/
    inline qElem(result,queue,elem)
    {
      result = queue.head;
      do
        :: (result != (queue.head + queue.length) % N) -> if
                                                    :: queue.q[result] == elem -> result = 1;
                                                                                  break;
                                                    :: else -> skip;
                                                   fi;
                                                   result = (result + 1) % N;
        :: else -> result = 0; break;
      od;
    }
/* Queue definitions END */
/* Array definition BEGIN */
    // Initialized with -1
    typedef Array{
    int a[N] = -1;
    }
/* Array definitions END*/
/* Message type for sent messages, see paper */
mtype = {REQUEST,PRIVILEGE, REPLY}
/* mailbox system for processes, one mailbox per process */
chan mailbox[N] = [M] of {mtype, int, int, Queue, Array};
/* each message contains  TYPE,  "j", "n", "Q" and "LN" (see paper), regardless of message type.
 * we just use dummy variables to fill unneeded variables of the respective message type. */


/* Variables of the algorithm, each as Array of size N, s.t.
 * each process uses its own array index */
bit havePrivilege[N] = 0, requesting[N] = 0;
int replyCount[N] = 0;
Queue Q[N];
Array RN[N], LN[N], requestCount[N];
//dummy vars for msgs
Queue dummyQ;
Array dummyA;

/* function p2 from paper */
inline p2(id, j, n)
{
  atomic {
    requestCount[id].a[j] = requestCount[id].a[j] + 1;
    if
      :: requestCount[id].a[j] == L -> 
        mailbox[j] ! REPLY,0,0,dummyQ,dummyA;
        requestCount[id].a[j] = 0;
      :: else -> skip;
    fi;
    if
      :: requestCount[id].a[j] == 1 ->
        RN[id].a[j] = n;
      :: else ->
        max(RN[id].a[j],n,RN[id].a[j]);
    fi;
    if
      :: (havePrivilege[id]
          && !requesting[id]
          && RN[id].a[j] == (LN[id].a[j] +1)%L) -> havePrivilege[id] = 0;
                                           mailbox[j] ! PRIVILEGE,0,0,Q[id],LN[id];
      :: else -> skip;
    fi
  }
}

inline p3(id)
{
    atomic{
        replyCount[id] = replyCount[id] + 1;
    }
}

/* Utility functions */

/* calculate maximum: out = max(qwe,asd) */
inline max(qwe, asd, out)
{
  if :: (qwe > asd) -> out = qwe;
     :: else -> out = asd;
  fi
}

/* broadcast msg of type msgtype with contents a and b to 1..N except to from. */
inline broadcast(counter, msgtype, a,b, from)
{
  atomic {
    counter = 0;
    do :: (counter < N) -> if :: (counter == from) -> skip;
                              :: else -> 
                                if
                                 :: (msgtype == REQUEST) -> mailbox[counter]!msgtype,a,b,dummyQ,dummyA;
                                 :: (msgtype == PRIVILEGE) -> mailbox[counter]!msgtype,0,0,a,b;
                                fi;
                           fi; counter++;
       :: else -> break;
    od
  }
}

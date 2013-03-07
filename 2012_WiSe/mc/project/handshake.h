#define N 3

typedef Queue {
int q[N];
short head = 0;
short length = 0;
}

inline qEmpty(result,queue)
{
  result = (queue.length == 0);
}

inline qAppend(queue,elem)
{
  queue.q[(queue.head + queue.length) % N] = elem;
  queue.length++;
}

inline qPoll(result,queue)
{
  result = queue.q[queue.head];
  queue.head = (queue.head + 1) % N;
  queue.length--;
}

inline qElem(result,queue,elem)
{
  result = queue.head;
  do
    :: (result < queue.head + queue.length) -> if
                                                :: queue.q[result] == elem -> result = 1;
                                                                              break;
                                                :: else -> skip;
                                               fi;
                                               result++;
    :: else -> result = 0; break;
  od;
}

typedef Array{
int a[N] = -1;
}

mtype = {REQUEST,PRIVILEGE}

/* A matrix A of NxN channels of unbounded size: */
//typedef Arraychan {
//  chan ch[N] = [0] of {mtype, int, int, Queue, Array}; /* N channels of unbounded size */
//};
//Arraychan A[N];
chan mailbox[N] = [N] of {mtype, int, int, Queue, Array};

/* Variables of the algorithm, each as Array of size N */
bit havePrivilege[N] = 0, requesting[N] = 0;
//int j[N], n[N];
Queue Q[N];
Array RN[N], LN[N];

inline p1(id,_c,__c,j,n)
{
  
  requesting[id] = 1;
  if
    :: (havePrivilege[id] == 0) -> RN[id].a[id] = RN[id].a[id] + 1;
                               broadcast(_c,REQUEST,id,RN[id].a[id],id);
                               do :: mailbox[id] ?? PRIVILEGE,_,_,Q[id],LN[id] -> break;
                                  :: mailbox[id] ?? REQUEST,j,n -> p2(id,j,n);
                               od;
                               havePrivilege[id] = 1;
    :: else -> skip;
  fi;
  /* CS */
  incs++;
  skip; assert(incs <= 1); 
  incs--;
  LN[id].a[id] = RN[id].a[id];
  _c = 0;
  do
    :: (_c < N) -> if :: (_c == id) -> skip;
                      :: else       -> qElem(__c,Q[id],j);
                                       if
                                        :: (__c == 0 && RN[id].a[j] == LN[id].a[j]+1) -> qAppend(Q[id],j);
                                        :: else -> skip;
                                       fi;
                   fi;
                   _c++;
    :: else     -> break;
  od;
  qEmpty(_c,Q[id]);
  if
    :: _c   -> skip;
    :: else -> d_step {
                havePrivilege[id] = false;
                // send PRIVILEGE(tail(Q),LN) to node head(Q)
                qPoll(_c,Q[id]);
                mailbox[_c] ! PRIVILEGE,0,0,Q[id],LN[id];
               };
  fi;
  requesting[id] = 0;
}

inline p2(id, j, n)
{
  atomic {
    max(RN[id].a[j],n,RN[id].a[j]);
    if
      :: (havePrivilege[id]
          && !requesting[id]
          && RN[id].a[j] == LN[id].a[j] +1) -> havePrivilege[id] = 0;
                                           mailbox[j] ! PRIVILEGE,0,0,Q[id],LN[id];
      :: else -> skip;
    fi
  }
}

inline max(qwe, asd, out)
{
  if :: (qwe > asd) -> out = qwe;
     :: else -> out = asd;
  fi
}

inline broadcast(counter, msgtype, a,b, from)
{
  atomic {
    counter = 0;
    do :: (counter < N) -> if :: (counter == from) -> skip;
                              :: else -> if
                                           :: (msgtype == REQUEST) -> mailbox[counter]!msgtype,a,b;
                                           :: (msgtype == PRIVILEGE) -> mailbox[counter]!msgtype,0,0,a,b;
                                         fi;
                           fi; counter++;
       :: else -> break;
    od
  }
}


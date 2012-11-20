#define N 4
#define K 1
#define M 5

#define INITVALUE 1

/* A matrix A of MxM channels of size 1 Simulation: */
typedef Arraychan {
  chan ch[M] = [1] of {bit}; /* M channels of size 1 */
};
Arraychan A[M];

byte initialValues[M];
byte finalValues[M];

int barriercounter = 0;
// Sends a bit b from id "from" to all processes (including from itself)
inline broadcast(counter, b, from)
{
  atomic {
    counter = 0;
    do :: (counter < M) -> A[from].ch[counter]!b; counter++;
       :: else -> break;
    od
  }
}

// barrier auslösen falls barriercounter = 0 mod M
inline wait(barrier)
{
  d_step { barriercounter++; }
  (barriercounter < barrier) -> skip;
  barrier = barrier + M;
}

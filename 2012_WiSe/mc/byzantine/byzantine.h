// Number of reliable processes N
#define N 3
// Number of unreliable processes K
#define K 1
// Sum of both
#define M 4

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
       :: (counter >= M) -> break;
    od
  }
}

// barrier auslösen falls barriercounter = 0 mod M
inline wait(barrier)
{
  d_step { barriercounter++; }
  (barrier <= barriercounter) -> skip;
  barrier = barrier + M;
}

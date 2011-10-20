/****************************************************************************************
 * AUFGABE 6																			*
 ****************************************************************************************/
#include "aufgabe06.h"
#include "adl_TimerHandler.h"

#if defined(AUFGABE_06)

/****************************************************************************************
 * VARIABLEN																			*
 ****************************************************************************************/

/****************************************************************************************
 * FUNKTIONEN																			*
 ****************************************************************************************/
/* Variablen um den State auch in den asynchron aufgerufenen callbacks zur Verfuegung zu stellen */
u8 runs=0;
adl_tmr_t *att01;

void callback01(u8 id, void *context)
{
	char buf[100];
	wm_sprintf(buf, "%i. Hallo Welt\r\n", (runs+1));

	adl_atSendResponse(ADL_AT_UNS,buf);
	runs++;
	if (runs>9) {
		adl_tmrUnSubscribe(att01, callback01, ADL_TMR_TYPE_100MS);
	}
}
void aufgabe06()
{
	adl_atSendResponse(ADL_AT_UNS, "Hallo Welt\r\n");
	u8 i = 0;
	for(i = 0; i < 10; i++)
	{
		adl_atSendResponse(ADL_AT_UNS, "Hallo Welt\r\n");
	}

	att01 = adl_tmrSubscribe(true, 100, ADL_TMR_TYPE_100MS,callback01);
/*	adl_tmrSubscribe(false, 200, ADL_TMR_TYPE_100MS, callbackhandler); */
}

#endif

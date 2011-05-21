/****************************************************************************************
 * AUFGABE 7																			*
 ****************************************************************************************/
#include "aufgabe07.h"
#include "adl_TimerHandler.h"
#if defined(AUFGABE_07)

/****************************************************************************************
 * VARIABLEN																			*
 ****************************************************************************************/

/****************************************************************************************
 * FUNKTIONEN																			*
 ****************************************************************************************/
adl_tmr_t *att01 = (adl_tmr_t*)0 ;
u8 initialValue = 0, currentValue = 0;

void countdownCall() {
	// der eigentliche Countdown
	if ( (currentValue--) > 1) {
		ascii bu[50];
		wm_sprintf(bu, "+COUNTDOWN: %d\n", currentValue);
		adl_atSendResponse(ADL_AT_UNS, bu);

	} else {
		adl_tmrUnSubscribe(att01, countdownCall, ADL_TMR_TYPE_100MS);
		att01 = (adl_tmr_t*)0 ;
		ascii bufi[50];
		wm_sprintf(bufi, "+COUNTDOWN: Fertig\n");
		adl_atSendResponse(ADL_AT_UNS, bufi);

	}
}
void countdown(adl_atCmdPreParser_t *parameter) {
	// als erstes fragen wir die Art des Parameters ab, ob read oder para Kommando eingegeben wurde
	if (parameter->Type == ADL_CMD_TYPE_PARA) {

		adl_atSendResponse(ADL_AT_RSP, "\n");
		// att01 haelt den aktuellen Timer, wenn der Countdown beendet wurde dann ist att01 null
		if (att01==(adl_tmr_t*)0) {

			// den Parameter parsen
			ascii buf[10];
			wm_strGetParameterString(buf, parameter->StrData, 1);
			// Werte merken, kein sanatizing
			initialValue = wm_atoi(buf);
			currentValue = initialValue;

			att01 = adl_tmrSubscribe(true,10, ADL_TMR_TYPE_100MS, countdownCall);

			adl_atSendResponse(ADL_AT_RSP, "\nOK\n");
		}
		else {
			// att01 ist nicht null, also duerfen wir auch keinen neuen Countdown einstellen
			adl_atSendResponse(ADL_AT_RSP, "\nERROR TIMER ALREADY ACQUIRED\n");
		}
	}
	// der read command Teil:
	if (parameter->Type == ADL_CMD_TYPE_READ) {
		// Wenn ein Timer aktiv ist koennen wir den Initialwert ausgeben
		if (att01!=(adl_tmr_t*)0) {

			ascii buf[100];
			wm_sprintf(buf, "\n+INITIAL VALUE: %i\n OK\n", initialValue);

			adl_atSendResponse(ADL_AT_RSP, buf);

		}
		else {
			//kein timer aktiv
			adl_atSendResponse(ADL_AT_RSP, "\nNO TIMER!\n");

		}

	}
}

void aufgabe07()
{
	// Vergessen Sie nicht, in der appli.h die Aufgabe 7 zu aktivieren... done!
	u16 mask = ADL_CMD_TYPE_PARA | ADL_CMD_TYPE_READ | ADL_CMD_TYPE_ACT | 0x0011 ;
	adl_atCmdSubscribe("at+countdown", countdown, mask);
}

#endif

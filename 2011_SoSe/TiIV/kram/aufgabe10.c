/****************************************************************************************
 * AUFGABE 10																			*
 ****************************************************************************************/
#include "aufgabe10.h"
#include "adl_gpio.h"

#ifdef AUFGABE_10
/****************************************************************************************
 * VARIABLEN																			*
 ****************************************************************************************/

u8 counter = 20;
bool ledState = TRUE, callRegistered = FALSE;

u32 myGPIOLabel = 1;

adl_ioDefs_t GPIOCfg[] = { ADL_IO_GPIO | 22 | ADL_IO_DIR_OUT | ADL_IO_LEV_LOW };

s32 GPIOEventHdl, GPIOHdl;

adl_tmr_t *timerHandler;
adl_ioDefs_t toWrite = ADL_IO_GPIO | 22;

/****************************************************************************************
 * FUNKTIONEN																			*
 ****************************************************************************************/

void hangUp(u8 ID, void* context) {
	adl_callHangup();

	adl_atCmdSend("ath",NULL,NULL);
	adl_atSendResponse(ADL_AT_UNS,"aufgelegt!");
	callRegistered = FALSE;
}

void countdown(u8 Id, void *context)
{
	counter--;

	ascii buf[50];
	wm_sprintf(buf, "+Counter: %i\r\n", counter);
	adl_atSendResponse(ADL_AT_RSP, buf);

	adl_ioWriteSingle(GPIOHdl, &toWrite, ledState);

	ledState = !ledState;

	if (counter <= 0)
	{
		adl_tmrUnSubscribe(timerHandler, countdown, ADL_TMR_TYPE_100MS);
		ledState = TRUE;

		adl_atSendResponse(ADL_AT_RSP, "call jens\r\n");
		adl_callSetup("015772862091", ADL_CALL_MODE_VOICE);
		adl_tmrSubscribe(FALSE, 90, ADL_TMR_TYPE_100MS, hangUp);
	}
}

s8 callHdlr(u16 Event,u32 Call_ID)
{

		adl_atSendResponse(ADL_AT_UNS, "incoming call\r\n");
		timerHandler = adl_tmrSubscribe(TRUE,10,ADL_TMR_TYPE_100MS, countdown);
		counter = 20;


	return 1;
}

bool clipHdlr(adl_atUnsolicited_t *paras) {
        ascii response[256];
        wm_strGetParameterString(response,paras->StrData,1);
		if(wm_strcmp(response,"+4915772862091") == 0 && !callRegistered){
			callRegistered = TRUE;
			counter = 20;
			adl_atSendResponse(ADL_AT_UNS,response);
			adl_atSendResponse(ADL_AT_UNS,"\r\n");
			timerHandler = adl_tmrSubscribe(TRUE,10,ADL_TMR_TYPE_100MS, countdown);
			adl_callHangup();
        }
        return FALSE;
}



void aufgabe10()
{
	GPIOHdl = adl_ioSubscribe(1, GPIOCfg, 0, 0, 0);

	// eigene Nummer übermitteln
	adl_atCmdSend("AT+CLIR=0", NULL, NULL);

	// CLIP aktivieren
	adl_atCmdSend("AT+CLIP=1", NULL, NULL);

	// auf die CLIP messages lauschen
	adl_atUnSoSubscribe("+CLIP:", (adl_atUnSoHandler_t) clipHdlr);


}

#endif

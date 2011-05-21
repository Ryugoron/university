/****************************************************************************************
 * AUFGABE 8																			*
 ****************************************************************************************/
#include "aufgabe08.h"
#include "adl_sms.h"
#ifdef AUFGABE_08

/****************************************************************************************
 * VARIABLEN																			*
 ****************************************************************************************/

/****************************************************************************************
 * FUNKTIONEN																			*
 ****************************************************************************************/

ascii phonenumber[30];

void callback() {
	adl_callSetupExt(phonenumber, ADL_CALL_MODE_VOICE, ADL_AT_UART1);
}

bool smsRcv(ascii* smstel, ascii* smstimelength, ascii* smstext) {

	adl_atSendResponse(ADL_AT_UNS, "\n");
	adl_atSendResponse(ADL_AT_UNS, "\n");
	adl_atSendResponse(ADL_AT_UNS, smstel);
	adl_atSendResponse(ADL_AT_UNS, "\n");
	adl_atSendResponse(ADL_AT_UNS, smstimelength);
	adl_atSendResponse(ADL_AT_UNS, "\n");
	adl_atSendResponse(ADL_AT_UNS, smstext);
	adl_atSendResponse(ADL_AT_UNS, "\n");

	//oh-oh, mehrere kurz hintereinander empfangene call sms koennen die Telefonnummer ueberschreiben
	if (wm_strcmp(smstext, "call")==0) {
		// number merken
		wm_strcpy(phonenumber, smstel);
		// Anruf schedulen
		adl_tmrSubscribe(false,30, ADL_TMR_TYPE_100MS,(adl_tmrHandler_t)callback);
		// sms nicht speichern
		return false;
	}
	else {
		// ansonsten sms speichern
		return true;
	}
}
void ctrhand(u8 event, u16 nb) {
	// wissen noch nicht was wir hier anfangen wollen
}
void aufgabe08()
{
	// Vergessen Sie nicht, in der init.c den Sim-Dienst zu starten, in dem Sie
	// INIT_CONFIG den Wert INIT_SIM zuweisen, sonst können Sie keine Anrufe und
	// SMS durch die Applikation starten... check!

	// am sms Dienst einschreiben
	adl_smsSubscribe(smsRcv, ctrhand, ADL_SMS_MODE_TEXT);
}


#endif

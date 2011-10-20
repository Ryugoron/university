/****************************************************************************************
 * AUFGABE 9																			*
 ****************************************************************************************/
#include "aufgabe09.h"
#include "adl_sms.h"
#ifdef AUFGABE_09

/****************************************************************************************
 * VARIABLEN																			*
 ****************************************************************************************/

/****************************************************************************************
 * FUNKTIONEN																			*
 ****************************************************************************************/
bool cmerHandler(adl_atResponse_t *paras) {
	adl_atSendResponse(ADL_AT_RSP, "cmer registered!!\n");
	return FALSE;
}

ascii date[30];
ascii alarm[100];
bool isReady = true;
adl_tmr_t *att01;
u8 h = 0;

void makeReady(u8 id, void *context) {
	isReady = TRUE;
}

bool smsHandler(ascii* tel, ascii* timeLength, ascii* text) {
	return TRUE;
}

void smsCtrlHandler(u8 ev, u16 nb) {

}

bool parseDate(adl_atResponse_t *paras) {
	wm_strGetParameterString(date, paras->StrData, 1);
	wm_strcat(alarm, date);
	wm_strcat(alarm, "\r\n");
	adl_atSendResponse(ADL_AT_RSP, alarm);
	if (isReady) {
		isReady = FALSE;
		adl_smsSend(h,"015772862091", alarm, ADL_SMS_MODE_TEXT);
		att01 = adl_tmrSubscribe(FALSE, 600, ADL_TMR_TYPE_100MS, makeReady);
	}
	return FALSE;
}

bool contactClosed(adl_atUnsolicited_t *t) {
	wm_strcpy(alarm, "Kontakt geschlossen am: ");
	adl_atCmdCreate("AT+CCLK?", FALSE ,parseDate,"+CCLK:",NULL);
	return FALSE;
}

bool contactOpened(adl_atUnsolicited_t *t) {
	wm_strcpy(alarm, "Kontakt geöffnet am: ");
	adl_atCmdCreate("AT+CCLK?", FALSE ,parseDate,"+CCLK:",NULL);
	return FALSE;
}

void aufgabe09() {
//unsolicited result listen
	adl_atCmdSend("AT+CMER=,1",NULL,NULL);
	adl_atUnSoSubscribe("+CKEV: 0,1", contactClosed);
	adl_atUnSoSubscribe("+CKEV: 0,0", contactOpened);
	h = adl_smsSubscribe(smsHandler, smsCtrlHandler, ADL_SMS_MODE_TEXT);

}

#endif

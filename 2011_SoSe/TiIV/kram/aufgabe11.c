/****************************************************************************************
 * AUFGABE11																			*
 ****************************************************************************************/
#include "aufgabe11.h"
#include "adl_flash.h"

#ifdef AUFGABE_11

/****************************************************************************************
 * VARIABLEN																			*
 ****************************************************************************************/
ascii currentIMSI[20]; // Aktuelle IMSI

/****************************************************************************************
 * FUNKTIONEN																			*
 ****************************************************************************************/
ascii* hdl = "diebstahl";
ascii number[20];
ascii emergency_nr[20];
s8 smsHandle = 0;

void cryoutloud(u8 id, void *context) {
	info("ich wurde gestohlen, hülfe!!");
	info(emergency_nr);
	adl_smsSend(smsHandle, emergency_nr, "Ich liege allein in einer Bar, GIZMODO komm und rette mich!!!", ADL_SMS_MODE_TEXT);
}
void protect()
{
	info("started...\r\n");
	s32 count = adl_flhExist(hdl, 0);
	if(count > OK)
	{
		info("exist OK\r\n");

		//hier gibt es unsere Nummer
		s8 read = adl_flhRead(hdl, 0, sizeof(currentIMSI), (u8*) number);
		adl_flhRead(hdl, 1, sizeof(emergency_nr), (u8*) emergency_nr);
		if(read == OK)
		{
			info("read OK\r\n");

			ascii buf[50];
			wm_sprintf(buf, "read:  %i\r\n", read);
			info(buf);

			wm_sprintf(buf, "IMSI: %s\r\n", number);
			info(buf);
			s16 compare = wm_strcmp(currentIMSI,emergency_nr);
			if (compare!=0) {
				adl_tmrSubscribe(FALSE,100,ADL_TMR_TYPE_100MS,cryoutloud);
			}
			else {

			}
		}
		else
		{

			ascii buf[50];
			wm_sprintf(buf, "read failed...guru meditation: %i\r\n", read);
			info(buf);
		}

	}
	else
	{
		// wenn wir hier sind gibt es unseren Eintrag nicht
		s8 hudl = adl_flhSubscribe(hdl, 2);
		if(hudl == OK)
		{ //wenn wir hier sind, sind wir subscribed
			info("Phone not protected! Writing number in flash\r\n");
			info(currentIMSI);
			info("\r\n");

			s8 writeSuccess = adl_flhWrite(hdl, 0, sizeof(currentIMSI), (u8*) currentIMSI);

			if(writeSuccess == OK)
			{
				info("write success\r\n");
			}
			else
			{
				ascii buf[50];
				wm_sprintf(buf, "write failed...guru meditation: %i\r\n", writeSuccess);

				info(buf);
			}
		}
		else
		{
			ascii duf[50];
			wm_sprintf(duf, "subscribe failed...guru meditation: %i\r\n", hudl);
			info(duf);
		}

	}

}
bool imsiHandler(adl_atResponse_t * Parameter)
{
	// Mit wm_strGetParameterString kann man den Parameter nicht ermitteln, da die
	// Antwort nicht das erwartete Präfix +CIMI: enthält

	// Speichert den Parameter
	ascii buffer[20];

	// Die Antwort wird in den Buffer kopiert
	wm_strcpy(buffer, Parameter->StrData);

	// Zeilenumbrüche werden aus dem String entfernt
	wm_strRemoveCRLF(buffer, buffer, (u16) wm_strlen(buffer));

	// Falls Antwort OK, passiert nichts
	if(!wm_strcmp(buffer, "OK"))
		// Keine Weiterleitung der Antwort
		return FALSE;
	else
		// IMSI wird gespeichert
		wm_strcpy(currentIMSI, buffer);

	// String wird sicherheitshalber terminiert
	currentIMSI[19] = '\0';

	// ... Fortsetzung der Lösung
	protect();

	// Keine Weiterleitung der Antwort
	return FALSE;
}
void emergency(adl_atCmdPreParser_t *parameter) {
	// als erstes fragen wir die Art des Parameters ab, ob read oder para Kommando eingegeben wurde
	if (parameter->Type == ADL_CMD_TYPE_PARA) {

		adl_atSendResponse(ADL_AT_RSP, "\n");
		s32 count = adl_flhExist(hdl, 1);
		if(count > OK)
		{
			info("exist OK\r\n");

			//hier gibt es unsere Nummer
			s8 read = adl_flhRead(hdl, 1, sizeof(emergency_nr), (u8*) emergency_nr);

			if(read == OK)
			{
				info("read OK\r\n");

				ascii buf[50];
				wm_sprintf(buf, "read:  %i\r\n", read);
				info(buf);

				wm_sprintf(buf, "emergency_nr: %s\r\n", number);
				info(buf);
			}
			else
			{

				ascii buf[50];
				wm_sprintf(buf, "read failed...guru meditation: %i\r\n", read);
				info(buf);
			}

		}
		else
		{
			// wenn wir hier sind gibt es unseren Eintrag nicht
				info("Emergency_nr not set\r\n");
				info("\r\n");
				wm_strGetParameterString(emergency_nr, parameter->StrData, 1);
				s8 writeSuccess = adl_flhWrite(hdl, 1, sizeof(emergency_nr), (u8*) emergency_nr);

				if(writeSuccess == OK)
				{
					info("write success\r\n");
				}
				else
				{
					ascii buf[50];
					wm_sprintf(buf, "write failed...guru meditation: %i\r\n", writeSuccess);

					info(buf);
				}

		}
	}

}
bool smsRcv(ascii* smstel, ascii* smstimelength, ascii* smstext) {
	return true;
}
void ctrhand(u8 event, u16 nb) {
	ascii buf[20];
	wm_sprintf(buf, "%i %i, %i,%i,%i", event,nb, ADL_SMS_EVENT_SENDING_ERROR, ADL_SMS_EVENT_SENDING_MR, ADL_SMS_EVENT_SENDING_OK);
	info(buf);
	// wissen noch nicht was wir hier anfangen wollen
}

void aufgabe11()
{
	u16 mask = ADL_CMD_TYPE_PARA | ADL_CMD_TYPE_ACT | 0x0011 ;
	adl_atCmdSubscribe("at+emergency_nr", emergency, mask);
	// IMSI ermitteln
	adl_atCmdSend("AT+CIMI", imsiHandler, "", NULL);
	/**
	 * ascii buf[50];
	 wm_sprintf(buf, "subscribe failed...guru meditation: %i\r\n", hudl);
	 */
	//ascii cmds[150];
	//		wm_sprintf(cmds, "return values: OK = %i, ADL_RET_ERR_PARAM = %i, ADL_RET_ERR_ALREADY_SUBSCRIBED = %i, ADL_FLH_RET_ERR_NO_ENOUGH_IDS = %i, ADL_RET_ERR_SERVICE_LOCKED = %i\r\n", OK, ADL_RET_ERR_PARAM, ADL_RET_ERR_ALREADY_SUBSCRIBED, ADL_FLH_RET_ERR_NO_ENOUGH_IDS, ADL_RET_ERR_SERVICE_LOCKED);
	//info(cmds);
	smsHandle = adl_smsSubscribe(smsRcv, ctrhand, ADL_SMS_MODE_TEXT);
	info("RUNLVL 02");

}

#endif

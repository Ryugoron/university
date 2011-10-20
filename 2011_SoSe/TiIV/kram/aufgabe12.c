/****************************************************************************************
 * AUFGABE12																			*
 ****************************************************************************************/
#include "aufgabe12.h"
#include "wip_inet.h"

#ifdef AUFGABE_12

#define MAX_BUFFLEN 50
/****************************************************************************************
 * VARIABLEN																			*
 ****************************************************************************************/
wip_channel_t channel;
ascii toSend[MAX_BUFFLEN];
u32 bufSend;
ascii toRead[MAX_BUFFLEN];
u32 readBufferLength;
/****************************************************************************************
 * FUNKTIONEN																			*
 ****************************************************************************************/

void echocallback( wip_event_t *ev, void *ctx){
	if (ev->kind == WIP_CEV_OPEN){
		int bytesWritten = wip_write(channel,toSend,bufSend);
		if (bytesWritten > 0){
		}else{
			switch (bytesWritten){
				case WIP_CERR_CSTATE:
					break;
				case WIP_CERR_NOT_SUPPORTED:
					break;
			}
		}
	}
	if (ev->kind == WIP_CEV_READ){
		readBufferLength = wip_read(channel, toRead, MAX_BUFFLEN);
		if (readBufferLength > 0){
			ascii msg[MAX_BUFFLEN + 10];
			wm_sprintf(msg, "+ECHO: %s\r\n", toRead);
			adl_atSendResponse(ADL_AT_RSP, msg);
		}
		wip_closeAndClear(&channel);
	}
	if (ev->kind == WIP_CEV_WRITE){
	}
}

void echocmd(adl_atCmdPreParser_t *parameter)
{
	if (parameter->Type == ADL_CMD_TYPE_PARA)
	{
		ascii *buf = ADL_GET_PARAM(parameter,0);
		int i;
		for (i = 0; i < MAX_BUFFLEN && buf[i]!='\0'; ++i) {toSend[i] = buf[i];}
		if (buf[i]=='\0')
			bufSend=i+1;
		channel = wip_TCPClientCreate("hwp.mi.fu-berlin.de", 50008, echocallback, NULL);
	}
}

void aufgabe12()
{
	// Vergessen Sie nicht, in der init.c den GPRS-Dienst zu starten, in dem Sie
	// INIT_CONFIG den Wert INIT_GPRS zuweisen, sonst können Sie keine Verbindung zum
	// Internet in Ihrer Anwendung aufbauen ... check!
	adl_atCmdSubscribe("AT+ECHO", echocmd, ADL_CMD_TYPE_PARA | 0x0011);
}

#endif

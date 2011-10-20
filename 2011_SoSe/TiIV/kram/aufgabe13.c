/****************************************************************************************
 * AUFGABE13																			*
 ****************************************************************************************/
#include "aufgabe13.h"
#ifdef AUFGABE_13

/****************************************************************************************
 * VARIABLEN																			*
 ****************************************************************************************/

/****************************************************************************************
 * FUNKTIONEN																			*
 ****************************************************************************************/
#define MAX_BUFFLEN 50

u32 readBufferLength;
wip_channel_t channel;
bool leds[8];
ascii toSend[MAX_BUFFLEN];
ascii toRead[MAX_BUFFLEN];
u32 bufSend = 0;
adl_ioDefs_t GPIOCfg[] = { 	ADL_IO_GPIO | 19 | ADL_IO_DIR_OUT | ADL_IO_LEV_LOW,
							ADL_IO_GPIO | 20 | ADL_IO_DIR_OUT | ADL_IO_LEV_LOW,
							ADL_IO_GPIO | 21 | ADL_IO_DIR_OUT | ADL_IO_LEV_LOW,
							ADL_IO_GPIO | 22 | ADL_IO_DIR_OUT | ADL_IO_LEV_LOW
	};
adl_ioDefs_t toWrite[4] = { ADL_IO_GPIO | 19, ADL_IO_GPIO | 20, ADL_IO_GPIO | 21, ADL_IO_GPIO | 22 };

s32 GPIOEventHdl, GPIOHdl;

void swleds() {
	int i = 0;
	for (i = 0; i<4; i++) {
		adl_ioWriteSingle(GPIOHdl, &toWrite[i], leds[i]);
	}
}
void echocallback( wip_event_t *ev, void *ctx){
	int i = 0;

	wm_sprintf(toSend, "SET:");
	for ( i = 0; i<8;i++) {
		toSend[i+4]=leds[i]?'1':'0';
	}
	bufSend=i+2+3;

	toSend[bufSend-1]=';';
	toSend[bufSend]='\0';

	info(toSend);

	if (ev->kind == WIP_CEV_OPEN){
		int bytesWritten = wip_write(ev->channel,toSend,bufSend);
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
		readBufferLength = wip_read(ev->channel, toRead, MAX_BUFFLEN);
		if (readBufferLength > 0){
			ascii msg[MAX_BUFFLEN + 10];
			wm_sprintf(msg, "+ECHO: %s\r\n", toRead);
//			adl_atSendResponse(ADL_AT_RSP, msg);
			int j = 0;
			for (j=4; j<12;j++) {
				leds[j-4] = toRead[j]=='1'?TRUE:FALSE;
			}
			swleds();
		}
		wip_closeAndClear(&channel);
	}
	if (ev->kind == WIP_CEV_WRITE){
	}
}
void countdown(u8 Id, void *context)
{
	wip_TCPClientCreate("hwp.mi.fu-berlin.de", 50008, echocallback, NULL);

}
void aufgabe13()
{
	int h = 0;
	for (h = 0; h<8; h++)
		leds[h]=FALSE;
	//leds[2]=TRUE;
	//leds[3]=TRUE;
	//leds[1]=TRUE;
	GPIOHdl = adl_ioSubscribe(4, GPIOCfg, 0, 0, 0);
	//swleds();
	adl_tmrSubscribe(TRUE, 200, ADL_TMR_TYPE_100MS, countdown);
}

#endif

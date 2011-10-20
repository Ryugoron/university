/****************************************************************************************
 * INIT																					*
 ****************************************************************************************
 * Setzt die Rahmenbedingungen der Applikation: Name, Firma & Version der Applikation	*
 * werden vergeben. Der Kontext von High und/oder Low Level Iterrupt Handler wird 		*
 * aktiviert und konfiguriert. Die Anzahl der Tasks und deren Attribute werden			*
 * festgelegt.																			*
 ****************************************************************************************/
#include "init.h"

/****************************************************************************************
 * DEFINITIONEN																			*
 ****************************************************************************************/
#define INIT_NONE		0x00
#define INIT_SIM		0x01
#define INIT_GPRS		0x02

/*
 * Legt fest, welche Funktionen vor dem Start der Anwenderapplikation initialisiert
 * werden:
 * 	INIT_NONE	Die Anwenderapplikation startet sofort
 * 	INIT_SIM	Start der Anwenderapplikation nach Registration im Netz
 * 	INIT_GPRS	Start der Anwenderapplikation nach Aktivierung der GPRS Funktion
 */
#define INIT_CONFIG		INIT_GPRS

/*
 * Einstiegspunkt(e) der Anwenderapplikation
 */
void        init_type(void);
extern void appli_mainTask(void);

/****************************************************************************************
 * VARIABLEN																			*
 ****************************************************************************************/
/*
 * Name der Applikation, der von der Firmware ausgelesen werden kann. Ist der Name nicht
 * definiert, wird er automatisch als leerer String festgesetzt.
 */
const ascii adl_InitApplicationName[]    = "Rahmenwerk";

/*
 * Hersteller der Applikation, der von der Firmware ausgelesen werden kann. Ist der
 * Hersteller nicht definiert, wird er automatisch als leerer String festgesetzt.
 */
const ascii adl_InitCompanyName[]        = "FU Berlin";

/*
 * Version der Applikation, die von der Firmware ausgelesen werden kann. Ist die Version
 * nicht definiert, wird sie automatisch als leerer String festgesetzt.
 */
const ascii adl_InitApplicationVersion[] = "v2010.04.01";

/*
 * Application Task Tabelle. Jeder Eintrag der Tabelle initialisiert einen Task mit
 * eigenem CallStack und wird beim Booten der CPU nach absteigender Priorität gestartet.
 * Der letzte Eintrag darf nur aus Nullen bestehen und der erste Task muss die höchste
 * Priorität haben. Es können mindestens eine und höchstens 64 Tasks gestartet werden.
 * Dabei darf jeder Prioritätswert nur ein mal vergeben werden, 1 ist der kleinste und
 * die Anzahl der Tasks in der Tabelle der größte mögliche Wert.
 *
 * Die CallStack Größe der Applikation legt den verfügbaren Speicher für lokale
 * Variablen und Funktionsrücksprungadressen fest. Im Runtime Environment Modus wird
 * der Wert ignoriert und das RAM des Rechners verwendet. Es ist dann unwahrscheinlich,
 * dass es zu Stackoverflows kommt. ARM Compiler brauchen weniger RAM als GCC oder GNU
 * (Standard C Library) Compiler.
 *
 * Die EntryPoint Funktion initialisiert den Task, beendet ihn aber bei verlassen nicht.
 *
 * Einträge für die Task Tabelle
 * typedef struct {
 *		void (* EntryPoint)(void);		// Funktionspointer zum Handler
 * 		u32 StackSize;					// Größe des CallStacks
 * 		const ascii* Name;				// Name des Tasks
 * 		u8 Priority;					// Priorität
 * } adl_InitTasks_t;
 */
const adl_InitTasks_t adl_InitTasks [] =
{
		{init_type,      4096, "MainTask", 1},
		{0, 0, 0, 0 }
};

/*
 * Der Pin der Simkarte
 */
const ascii * initSimPin = "1234";

/*
 * Die Zugangsdaten für die GPRS Verbindung
 */
const ascii * initGprsApn = "pinternet.interkom.de";	// Name des Zugriffspunktes
const ascii * initGprsUsr = "";							// Benutzer
const ascii * initGprsPwd = "";							// Passwort

/*
 * Speichert Informationen über die GPRS Verbindung
 */
wip_bearer_t init_gprsBearer;

/*
 * Speichert Informationen über den GPS Empfänger
 */
adl_ioDefs_t  gps_io_label = ADL_IO_GPIO | 23 | ADL_IO_DIR_OUT | ADL_IO_LEV_HIGH;
adl_ioDefs_t  gps_io_write = ADL_IO_GPIO | 23;
s32           gps_io_handle;
s8            gps_fcm_handle;

/*
 * Speichert GPS Daten, die automatisch aktualisiert werden jederzeit abgerufen werden
 * können im NMEA GPRMC Datenformat.
 */
ascii * gps_data;

/****************************************************************************************
 * FUNKTIONEN																			*
 ****************************************************************************************/
/*
 * Handler für die Aktivierung der GPRS Funktion
 */
void init_gprsHandler(wip_bearer_t Bearer, s8 Event, void * Context)
{
	switch(Event)
	{
	case WIP_BEV_CONN_FAILED:
		info("Verbindungsfehler, WIP_BOPT_ERROR enthält den Grund des Fehlers");
		break;
	case WIP_BEV_IP_CONNECTED:
		info("GPRS Verbindung wurde eingerichtet");
		//info("Die Anwenderapplikation wird gestartet");
		appli_mainTask();

		break;
	case WIP_BEV_IP_DISCONNECTED:
		info("GPRS Verbindung wurde getrennt, WIP_BOPT_ERROR enthält den Grund der "
		     "Trennung");
		break;
	case WIP_BEV_STOPPED:
		info("Verbindung wurde nach dem Aufruf von wip_bearerStop vollständig "
		     "beendet");
		break;
	}
}

/*
 * Richtet GPRS Verbindung ein
 */
void initGprs()
{
	// Speichert den Rückgabewert der WIP Funktionsaufrufe
	s8 result;

	// Initialisiert den TCP Stack. Auf eine Auswertung des Rückgabewertes wurde
	// verzichtet, da sich das Modul bei bei einem Fehler resettet
	wip_netInit();

	// Meldet den Handler für die GPRS Verbindung an
	wip_bearerOpen(
		&init_gprsBearer,
		"GPRS",
		init_gprsHandler,
		NULL
	);

	// Konfiguriert die GPRS Verbindung
	result =
		wip_bearerSetOpts(
			init_gprsBearer,
			WIP_BOPT_GPRS_APN,	initGprsApn,
			WIP_BOPT_LOGIN,		initGprsUsr,
			WIP_BOPT_PASSWORD,	initGprsPwd,
			WIP_BOPT_END
		);

	// Behandelt die Konfiguration
	switch(result)
	{
	case OK:
		//info("TCP Stack wurde initialisiert");
		break;
	case WIP_BERR_NO_DEV:
		info("Die Geräteschnittstelle existiert nicht");
		break;
	case WIP_BERR_ALREADY:
		info("Die Geräteschnittstelle wurde bereits geöffnet");
		break;
	case WIP_BERR_NO_IF:
		info("Die Netzwerkschnittstelle ist nicht verfügbar");
		break;
	case WIP_BERR_NO_HDL:
		info("Der Handler ist unbekannt");
		break;
	}

	// Baut die GPRS Verbindung auf
	result =
		wip_bearerStart(init_gprsBearer);

	// Behandlet den Verbindungsaufbau
	switch(result)
	{
	case OK:
		//info("Verbindung wird eingerichtet");
		break;
	case WIP_BERR_OK_INPROGRESS:
		//info("Verbindung gestartet, ein Ereignis folgt bei Beendingung");
		break;
	case WIP_BERR_BAD_HDL:
		info("Ungültiger Handle");
		break;
	case WIP_BERR_BAD_STATE:
		info("Die Verbindung wurde nicht gestoppt");
		break;
	case WIP_BERR_DEV:
		info("Fehler bei der Initialisierung der Verbindungsschicht");
		break;
	}
}

void poll_creg(u8 ID, void * Context);

/*
 *
 */
bool poll_creg_handler (adl_atResponse_t *Rsp) {
    ascii *rsp;
    ascii regStateString[3];
    s32 regStateInt;

	info("Warten auf GPRS Verbindung...");

    rsp = (ascii *)adl_memGet(Rsp->StrLength);
    wm_strRemoveCRLF(rsp, Rsp->StrData, Rsp->StrLength);

    wm_strGetParameterString(regStateString, Rsp->StrData, 2);
    regStateInt = wm_atoi(regStateString);

    if ( 1 == regStateInt || 5 ==regStateInt) {
		initGprs();
    } else {
      /* Not ready yet, we'll check again later. Set a one-off timer. */
      adl_tmrSubscribe( FALSE, 10, ADL_TMR_TYPE_100MS,
                        poll_creg);
    }
    return FALSE;
}

/*
 *
 */
void poll_creg(u8 ID, void * Context)
{
	adl_atCmdCreate (
		"AT+CREG?",
		FALSE,
		poll_creg_handler,
		ADL_STR_CREG,
		NULL
	);
}

/*
 * Handler für die Registration im Netz
 */
void initSimHandler(u8 Event)
{
	switch(Event)
	{
	/* Normale Ereignisse */
	case ADL_SIM_EVENT_PIN_OK:
		info("Gültige Pin eingegeben");
		break;
	case ADL_SIM_EVENT_REMOVED:
		info("SIM Karte wurde entfernt");
		break;
	case ADL_SIM_EVENT_INSERTED:
		//info("SIM Karte wurde eingesteckt");
		break;
	case ADL_SIM_EVENT_FULL_INIT:
		info("Registration im Netz abgeschlossen");

		/* Start der Anwenderapplikation oder Aktivierung der GPRS Funktion */
		if(INIT_CONFIG == INIT_SIM)
			//info("Die Anwenderapplikation wird gestartet");
			appli_mainTask();
		else
			//info("GPRS wird vorbereitet");
			poll_creg(0, NULL);
		break;

	/* Fehler: Der Dienst wartet auf die Eingabe des PIN codes */
	case ADL_SIM_EVENT_PIN_ERROR:
		info("Pin ist ungültig");
		break;
	case ADL_SIM_EVENT_PIN_WAIT:
		info("Bei der Anmeldung des Dienstes ist Pin Code NULL");
		break;
	case ADL_SIM_EVENT_NET_LOCK:
		info("Modul ist im Netz gesperrt");
		break;
	default:
		info("Unbekanntes Ereignis");
		break;
	}
}

/*
 * Registriert das Modul im Netz
 */
void initSim()
{
	//info("Anmeldung des SIM Dienstes");
	s32 result =
		adl_simSubscribe(
			initSimHandler,
			(ascii *) initSimPin
		);

	//info("Behandlung des Rückgabewertes");
	switch(result)
	{
	case OK:
		//info("Anmeldung erfolgreich");
		break;
	case ADL_RET_ERR_PARAM:
		info("Handler ist NULL");
		break;
	case ADL_RET_ERR_ALREADY_SUBSCRIBED:
		info("Dienst wurde mit diesem Handler bereits angemeldet");
		break;
	case ADL_RET_ERR_SERVICE_LOCKED:
		info("Aufruf stammt von einem Low Level Interrupt Handler");
		break;
	default:
		info("Unbekanntes Ereignis");
		break;
	}
}

/*
 * Verarbeitet die Daten des GPS Empfängers
 */
bool gps_fcm_data_handler(u16 length, u8 *data)
{
	u16   index = 0;
	ascii buffer_in[256];
	ascii buffer_out[256];

	for (index = 0 ; index < length; index++)
		buffer_in[index] = (ascii)data[index];

	if(wm_strncmp(buffer_in, "$GPRMC,", 7) == 0)
	{
		for(index = 7; index < wm_strlen(buffer_in) && buffer_in[index] != '\r'; index++)
			buffer_in[index - 7] = buffer_in[index];
		buffer_in[index - 7] = '\0';
		wm_sprintf(buffer_out, "%s", buffer_in);
		gps_data = buffer_out;
	}

	return TRUE;
}

/*
 * Wechselt UART2 in den Datenmodus
 */
bool gps_fcm_ctrl_handler(adl_fcmEvent_e event)
{
	if(event == ADL_FCM_EVENT_FLOW_OPENNED)
		adl_fcmSwitchV24State(gps_fcm_handle, ADL_FCM_V24_STATE_DATA);
	return FALSE;
}

/*
 * Grund für den letzten Start der CPU
 */
void init_type()
{
	//info("Ermittelt den Startgrund");
	adl_InitType_e result =
			adl_InitGetType();

	//info("Behandelt des Startgrund");
	switch(result)
	{
	case ADL_INIT_POWER_ON:
		info("Startgrund: Normaler Start");
		break;
	case ADL_INIT_REBOOT_FROM_EXCEPTION:
		info("Startgrund: Neustart nach schwerem Fehler durch die ADL");
		break;
	case ADL_INIT_DOWNLOAD_SUCCESS:
		info("Startgrund: Neustart nach erfolgreicher Installation");
		break;
	case ADL_INIT_DOWNLOAD_ERROR:
		info("Startgrund: Neustart nach gescheiterter Installation");
		break;
	case ADL_INIT_RTC:
		info("Startgrund: Start durch Wecksignal der Echtzeituhr");
		break;
	default:
		info("Unbekanntes Ereignis");
		break;
	}

	// UART2 muss mit 'at+wmfm=0,1,2' aktiviert worden sein!

	// Konfiguriert UART2 Bus für den GPS Empfänger
	adl_atCmdCreate("AT+IPR=38400", ADL_AT_PORT_TYPE(ADL_PORT_UART2, FALSE), (adl_atRspHandler_t) NULL, NULL);
	adl_atCmdCreate("AT+ICF=3",     ADL_AT_PORT_TYPE(ADL_PORT_UART2, FALSE), (adl_atRspHandler_t) NULL, NULL);
	adl_atCmdCreate("AT+IFC=0,0",   ADL_AT_PORT_TYPE(ADL_PORT_UART2, FALSE), (adl_atRspHandler_t) NULL, NULL);

	// Aktiviert den GPS Empfänger
	gps_io_handle = adl_ioSubscribe(1, &gps_io_label, 0, 0, 0);
	adl_ioWriteSingle(gps_io_handle, &gps_io_write, TRUE);

	// Verarbeitet die Daten des GPS Empfängers
	gps_fcm_handle = adl_fcmSubscribe(ADL_PORT_UART2, gps_fcm_ctrl_handler, gps_fcm_data_handler);

	gps_data = ",,,,,,,,,,,";

	if(!INIT_CONFIG)
		//info("Die Anwenderapplikation wird gestartet");
		appli_mainTask();
	else
		//info("Das Modul wird im Netz registriert");
		initSim();
}

/*
 * Gibt eine Information auf der externen Applikation aus
 */
void info(ascii * Message)
{
	// Benötigter Speicher für die Formatierungen
	const static u8 overhead = 12;

	// Speicher für die Ausgabe der Nachricht
	ascii * buffer =
		(ascii *) adl_memGet(
			wm_strlen(Message)
			+ overhead
		);

	// Formatierung der Nachricht für die Ausgabe
	wm_sprintf(
		buffer,
		"\r\n+INFO: %s\r\n",
		Message
	);

	// Ausgabe der Nachricht
	adl_atSendUnsoResponse(
		ADL_PORT_UART1,
		buffer,
		FALSE
	);

	// Freigabe des Speichers
	adl_memRelease(buffer);
}

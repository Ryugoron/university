/****************************************************************************************
 * AUFGABE17																			*
 ****************************************************************************************/
#include "aufgabe17.h"
#ifdef AUFGABE_17

void spi2_init             (void);
void bus_subscribe_handler (s32 event);
void bus_control_handler   (s32 event);

/****************************************************************************************
 * VARIABLEN																			*
 ****************************************************************************************/
// Konfiguriert den Spi2 Bus zur Kommunikation mit dem SMB380
adl_busSPISettings_t spi2_settings =
	{
		1,								// Clock Speed			= 6,5 MHz
		ADL_BUS_SPI_CLK_MODE_3,			// Clock Mode			= 3
		ADL_BUS_SPI_ADDR_CS_GPIO,		// Chip Select			= GPIO
		ADL_BUS_SPI_CS_POL_LOW,			// Chip Select Polarity	= low
		ADL_BUS_SPI_MSB_FIRST,			// Lsb First			= MSB
		ADL_IO_GPIO | 35,				// Gpio Chip Select		= 35
		ADL_BUS_SPI_LOAD_UNUSED,		// Load Signal			= unused
		ADL_BUS_SPI_DATA_UNIDIR,		// Data Lines Config	= unidir
		ADL_BUS_SPI_MASTER_MODE,		// Master Mode			= Master
		ADL_BUS_SPI_BUSY_UNUSED			// Busy Signal			= unused
	};
adl_busAccess_t spi2_bus_access = {0,0};// Adress- und Opcodelänge
s32             spi2_handle;			// Spi Bus Handle
u32             spi2_address_length;	// Länge einer Adresse

/****************************************************************************************
 * FUNKTIONEN																			*
 ****************************************************************************************/
void aufgabe17()
{
	// Initialisiert die Verbindung zum SMB380
	spi2_init ();

	// Beispiel, wie man ein Register ausliest:
	u8 register_temperatur  = 0;

	// Adresse des Temperaturregisters
	spi2_bus_access.Address = 0x88000000;

	// Register lesen
	adl_busRead(
		spi2_handle,					// SPI2 Handle
		&spi2_bus_access,				// Übergabe der Adresse
		1,								// Anzahl zu lesender Register
		&register_temperatur			// Variable für das Ergebnis
	);

	// register_temperatur enthält nun die aktuelle Temperatur
}


/*
 * Konfiguriert Spi2 Bus
 */
//===========================================================================
void spi2_init (void)
//===========================================================================
	{
	s32 ret;

	spi2_handle =
		adl_busSubscribe(
			ADL_BUS_ID_SPI,				// Spi Bus
			2,							// Block ID 2
			&spi2_settings				// Einstellungen
		);
	bus_subscribe_handler(spi2_handle);// Rückgabewert behandeln

	spi2_address_length = 8;			// Adresslänge setzen
	ret =
		adl_busIOCtl(					// Adresslänge setzen
			spi2_handle,
			ADL_BUS_CMD_SET_ADD_SIZE,	// Kommando: Adresslänge setzen
			&spi2_address_length
		);
	bus_control_handler(ret);			// Rückgabewert behandeln

	spi2_address_length = 0;			// Adresslänge setzen
	ret =
		adl_busIOCtl(					// Opcodelänge setzen
			spi2_handle,
			ADL_BUS_CMD_SET_OP_SIZE,	// Kommando: Opcodelänge setzen
			&spi2_address_length		// Opcodelänge
		);
	bus_control_handler(ret);			// Rückgabewert behandeln
	}

/*
 * Behandelt Busanmeldung
 */
//===========================================================================
void bus_subscribe_handler (s32 event)
//===========================================================================
	{
	/* Normale Ereignisse */
	if (event > 0)						// Bus Handle
		{
		info("Spi2 Bus angemeldet");
		return;
		}

	/* Fehler */
	switch (event)						// Rückgabewert behandeln
		{
		case ADL_RET_ERR_PARAM:
			info("Ungültiger Parameter");
			break;
		case ADL_RET_ERR_ALREADY_SUBSCRIBED:
			info("Bus wurde schon vorher angemeldet");
			break;
		case ADL_RET_ERR_BAD_HDL:
			info("GPIO wurde schon angemeldet");
			break;
		case ADL_RET_ERR_NOT_SUPPORTED:
			info("Bus auf der Plattform nicht verfügbar");
			break;
		case ADL_RET_ERR_SERVICE_LOCKED:
			info("Aufruf aus Low Level Interrupt Handler");
			break;
		default:
			info("Unbekanntes Ereignis");
			break;
		}
	}

/*
 *	Behandelt Änderung der Konfiguration
 */
//===========================================================================
void bus_control_handler (s32 event)
//===========================================================================
	{
	switch (event)						// Rückgabewert behandeln
		{
		/* Normale Ereignisse */
		case OK:
			info("Bus Konfiguration erfoglreich");
			break;

		/* Fehler */
		case ADL_RET_ERR_PARAM:
			info("Ungültiger Parameter");
			break;
		case ADL_RET_ERR_UNKNOWN_HDL:
			info("Handle unbekannt");
			break;
		case ADL_RET_ERR_DONE:
			info("Konfiguration gescheitert");
			break;
		case ADL_RET_ERR_BAD_HDL:
			info("Kommando für den Bus nicht zulässig");
			break;
		case ADL_RET_ERR_NOT_SUPPORTED:
			info("Asynchroner Modus nicht möglich");
			break;
		case ADL_RET_ERR_SERVICE_LOCKED:
			info("Aufruf aus Low Level Interrupt Handler");
			break;
		default:
			info("Unbekanntes Ereignis");
			break;
		}
	}

#endif


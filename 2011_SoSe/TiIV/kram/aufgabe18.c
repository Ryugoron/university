/****************************************************************************************
 * AUFGABE18																			*
 ****************************************************************************************/
#include "aufgabe18.h"
#ifdef AUFGABE_18

void spi1_init             (void);
void bus_subscribe_handler (s32 event);
void bus_control_handler   (s32 event);

/****************************************************************************************
 * VARIABLEN																			*
 ****************************************************************************************/
// Signalverstärkung CC1100
u8 cc1100_pa_table[] =
	{
	0x00,							// -52 dBm
	0x23, 							// -15 dBm
	0x33, 							// -12 dBm
	0x34, 							// -10 dBm
	0x28, 							//  -8 dBm
	0x2B, 							//  -6 dBm
	0x57, 							//  -4 dBm
	0x54, 							//  -2 dBm
	0x3F, 							//   0 dBm
	0x8C, 							//  +2 dBm
	0x8A, 							//  +3 dBm
	0x87, 							//  +4 dBm
	0x84, 							//  +5 dBm
	0xCE, 							//  +6 dBm
	0xCC, 							//  +7 dBm
	0xC9, 							//  +8 dBm
	0xC6, 							//  +9 dBm
	0xC3  							// +10 dBm
	};
// Register CC1100
u8 cc1100_config[] =
	{
	0x06, 							// IOCFG2 (GDO2)
	0x2E, 							// IOCFG1 (GDO1)
	0x0E, 							// IOCFG0 (GDO0)
	0x0F, 							// FIFOTHR
	0x9B, 							// SYNC1
	0xAD, 							// SYNC0
	0x3F, 							// PKTLEN
	0x06, 							// PKTCTRL1
	0x45, 							// PKTCTRL0
	0xFF, 							// ADDR (0xFF alle)
	0x00, 							// CHANNR
	0x0B,							// FSCTRL1
	0x00, 							// FSCTRL0
	0x21,							// FREQ2
	0x71, 							// FREQ1
	0x7A,							// FREQ0
	0x2D, 							// MDMCFG4
	0xF8, 							// MDMCFG3
	0x73, 							// MDMCFG2
	0x42, 							// MDMCFG1
	0xF8, 							// MDMCFG0
	0x00, 							// DEVIATN
	0x07,							// MCSM2	// ++++++++
	0x03,							// MCSM1 (RX_OFFMODE = IDLE , TX_OFFMODE = IDLE)
	0x18, 							// MCSM0
	0x1D, 							// FOCCFG
	0x1C, 							// BSCFG
	0xC0, 							// AGCCTRL2
	0x49, 							// AGCCTRL1
	0xB2, 							// AGCCTRL0
	0x87, 							// WOREVT1
	0x6B, 							// WOREVT0
	0xF8, 							// WORCTRL // +++++++++
	0xB6,							// FREND1
	0x10, 							// FREND0
	0xEA, 							// FSCAL3
	0x2A,							// FSCAL2
	0x00,							// FSCAL1
	0x1F							// FSCAL0
	};
// Konfiguriert den Spi1 Bus zur Kommunikation mit dem SMB380
adl_busSPISettings_t spi1_settings =
	{
		1,								// Clock Speed			= 6,5 MHz
		ADL_BUS_SPI_CLK_MODE_3,			// Clock Mode			= 3
		ADL_BUS_SPI_ADDR_CS_GPIO,		// Chip Select			= GPIO
		ADL_BUS_SPI_CS_POL_LOW,			// Chip Select Polarity	= low
		ADL_BUS_SPI_MSB_FIRST,			// Lsb First			= MSB
		ADL_IO_GPIO | 31,				// Gpio Chip Select		= 31
		ADL_BUS_SPI_LOAD_UNUSED,		// Load Signal			= unused
		ADL_BUS_SPI_DATA_UNIDIR,		// Data Lines Config	= unidir
		ADL_BUS_SPI_MASTER_MODE,		// Master Mode			= Master
		ADL_BUS_SPI_BUSY_UNUSED			// Busy Signal			= unused
	};
adl_busAccess_t spi1_bus_access = {0,0};// Adress- und Opcodelänge
s32             spi1_handle;			// Spi Bus Handle
u32             spi1_address_length;	// Länge einer Adresse

/****************************************************************************************
 * FUNKTIONEN																			*
 ****************************************************************************************/
void aufgabe18()
{
	// Initialisiert die Verbindung zum CC1100
	spi1_init ();

	// Beispiel, wie man ein Register ausliest:
	u8 register_freq0  = 0;

	// Adresse des Temperaturregisters
	spi1_bus_access.Address = CC1100_FREQ0 | CC1100_READ_SINGLE;

	// Register lesen
	adl_busRead(
		spi1_handle,					// SPI1 Handle
		&spi1_bus_access,				// Übergabe der Adresse
		1,								// Anzahl zu lesender Register
		&register_freq0					// Variable für das Ergebnis
	);

	// register_temperatur enthält nun die aktuelle Temperatur
}


/*
 * Konfiguriert Spi1 Bus
 */
//===========================================================================
void spi1_init (void)
//===========================================================================
	{
	s32 ret;

	spi1_handle =
		adl_busSubscribe(
			ADL_BUS_ID_SPI,				// Spi Bus
			1,							// Block ID 1
			&spi1_settings				// Einstellungen
		);
	bus_subscribe_handler(spi1_handle);// Rückgabewert behandeln

	spi1_address_length = 8;			// Adresslänge setzen
	ret =
		adl_busIOCtl(					// Adresslänge setzen
			spi1_handle,
			ADL_BUS_CMD_SET_ADD_SIZE,	// Kommando: Adresslänge setzen
			&spi1_address_length
		);
	bus_control_handler(ret);			// Rückgabewert behandeln

	spi1_address_length = 0;			// Adresslänge setzen
	ret =
		adl_busIOCtl(					// Opcodelänge setzen
			spi1_handle,
			ADL_BUS_CMD_SET_OP_SIZE,	// Kommando: Opcodelänge setzen
			&spi1_address_length		// Opcodelänge
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
		info("Spi1 Bus angemeldet");
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

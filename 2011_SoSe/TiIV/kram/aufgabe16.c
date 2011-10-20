/****************************************************************************************
 * AUFGABE16																			*
 ****************************************************************************************/
#include "aufgabe16.h"
#ifdef AUFGABE_16

/****************************************************************************************
 * VARIABLEN																			*
 ****************************************************************************************/
extern ascii * gps_data;		// Periodisch aktualisierter String mit GPS Daten im
								// NMEA GPRMC Datenformat im Sekundentakt

/****************************************************************************************
 * FUNKTIONEN																			*
 ****************************************************************************************/

void aufgabe16()
{
	// Sie können den String 'gps_data' auslesen um ihre GPS Koordinaten zu erfahren. Die
	// ersten ausgelesenen Werte direkt nach dem Start könnten ungültig sein, da der GPS
	// Empfänger bis zu einer Minute braucht, um Satellitenkontakt zu finden.
	//
	// Typisches NMEA GPRMC Datenformat:
	//	191410,A,4735.5634,N,00739.3538,E,0.0,0.0,181102,0.4,E,A*19
	//	^      ^ ^           ^            ^   ^   ^      ^     ^
	//	|      | |           |            |   |   |      |     |
	//	|      | |           |            |   |   |      |     Neu in NMEA 2.3:
	//	|      | |           |            |   |   |      |     Art der Bestimmung
	//	|      | |           |            |   |   |      |     A=autonomous (selbst)
	//	|      | |           |            |   |   |      |     D=differential
	//	|      | |           |            |   |   |      |     E=estimated (geschätzt)
	//	|      | |           |            |   |   |      |     N=not valid (ungültig)
	//	|      | |           |            |   |   |      |     S=simulator
	//	|      | |           |            |   |   |      |
	//	|      | |           |            |   |   |      Missweisung (mit Richtung)
	//	|      | |           |            |   |   |
	//	|      | |           |            |   |   Datum: 18.11.2002
	//	|      | |           |            |   |
	//	|      | |           |            |   Bewegungsrichtung in Grad (wahr)
	//	|      | |           |            |
	//	|      | |           |            Geschwindigkeit über Grund (Knoten)
	//	|      | |           |
	//	|      | |           Längengrad mit (Vorzeichen)-Richtung (E=Ost, W=West)
	//	|      | |           007° 39.3538' Ost
	//	|      | |
	//	|      | Breitengrad mit (Vorzeichen)-Richtung (N=Nord, S=Süd)
	//	|      | 46° 35.5634' Nord
	//	|      |
	//	|      Status der Bestimmung: A=Active (gültig); V=void (ungültig)
	//	|
	//	Uhrzeit der Bestimmung: 19:14:10 (UTC-Zeit)
}

#endif

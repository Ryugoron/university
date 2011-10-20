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
	// Sie k�nnen den String 'gps_data' auslesen um ihre GPS Koordinaten zu erfahren. Die
	// ersten ausgelesenen Werte direkt nach dem Start k�nnten ung�ltig sein, da der GPS
	// Empf�nger bis zu einer Minute braucht, um Satellitenkontakt zu finden.
	//
	// Typisches NMEA GPRMC Datenformat:
	//	191410,A,4735.5634,N,00739.3538,E,0.0,0.0,181102,0.4,E,A*19
	//	^      ^ ^           ^            ^   ^   ^      ^     ^
	//	|      | |           |            |   |   |      |     |
	//	|      | |           |            |   |   |      |     Neu in NMEA 2.3:
	//	|      | |           |            |   |   |      |     Art der Bestimmung
	//	|      | |           |            |   |   |      |     A=autonomous (selbst)
	//	|      | |           |            |   |   |      |     D=differential
	//	|      | |           |            |   |   |      |     E=estimated (gesch�tzt)
	//	|      | |           |            |   |   |      |     N=not valid (ung�ltig)
	//	|      | |           |            |   |   |      |     S=simulator
	//	|      | |           |            |   |   |      |
	//	|      | |           |            |   |   |      Missweisung (mit Richtung)
	//	|      | |           |            |   |   |
	//	|      | |           |            |   |   Datum: 18.11.2002
	//	|      | |           |            |   |
	//	|      | |           |            |   Bewegungsrichtung in Grad (wahr)
	//	|      | |           |            |
	//	|      | |           |            Geschwindigkeit �ber Grund (Knoten)
	//	|      | |           |
	//	|      | |           L�ngengrad mit (Vorzeichen)-Richtung (E=Ost, W=West)
	//	|      | |           007� 39.3538' Ost
	//	|      | |
	//	|      | Breitengrad mit (Vorzeichen)-Richtung (N=Nord, S=S�d)
	//	|      | 46� 35.5634' Nord
	//	|      |
	//	|      Status der Bestimmung: A=Active (g�ltig); V=void (ung�ltig)
	//	|
	//	Uhrzeit der Bestimmung: 19:14:10 (UTC-Zeit)
}

#endif

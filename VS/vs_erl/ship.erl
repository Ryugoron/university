-module(ship).
-export([start/1,dock/2,travel/2]).

dock(Ship,Planet) -> Ship!{cmd_travel,Planet}.

travel(Ship,Planet) -> Ship!{cmd_travel,Planet}.

start(Name) -> spawn(fun(ID) -> ship_loop(ID,unknown) end,[Name]).

ship_loop(MyName, SeedPlanet) ->
	receive
		{cmd_travel, Planet} -> 
			if SeedPlanet /= unknown -> SeedPlanet!{undock,self()} end,
			Planet!{dock,self(), MyName},
			ship_loop(MyName, unknown);
		{kcod,Planet} -> ship_loop(MyName, Planet);
		
		_ -> io:format("~w~n~w~n",["Unbekannte Nachricht erhalten.","Droppe diese"]),
			ship_loop(MyName, SeedPlanet)
	end.

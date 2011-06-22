-module(ship).
-export([start/0,dock/2,travel/2]).

dock(Ship,Planet) -> Ship!{cmd_travel,Planet}.

travel(Ship,Planet) -> Ship!{cmd_travel,Planet}.

start() -> spawn(fun() -> ship_loop(unknown) end).

ship_loop(SeedPlanet) ->
	receive
		{cmd_travel, Planet} -> 
			if SeedPlanet /= unknown -> SeedPlanet!{undock,self()} end,
			Planet!{dock,self()},
			ship_loop(unknown);
		{kcod,Planet} -> ship_loop(Planet)
	end.

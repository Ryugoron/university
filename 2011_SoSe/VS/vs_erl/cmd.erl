-module(cmd).
-export([start/0,newShip/1,newPlanet/1,connect/1,connect/2,peers/0,peers/1,dock/2,travel/1,travel/2]).
-import(planet, [start/1]).

%%
%% This Module helps using the the planets and ships in this Galaxy.
%%
%% You may refere the Ships, Planets by Name or just take the last
%% assoziated Ship/Planet
%%
%% The CMD also contains a mapping for the process identities in this
%% galaxy
%%

newShip(Name) -> 
	PID = ship:start(Name),
	msg!{newS,Name,PID}.

newPlanet(Name) -> 
	PID = planet:start(Name),
	msg!{newP,Name,PID}.	

connect(Planet) -> msg!{connect,Planet}.

connect(Planet1,Planet2)-> msg!{connect, Planet1, Planet2}.

peers() -> msg!peers.

peers(Planet) -> msg!{peers,Planet}.

dock(Ship,Planet) -> msg!{dock,Ship,Planet}.

travel(Planet) -> msg!{travel,Planet}.

travel(Ship,Planet) -> msg!{travel,Ship,Planet}.

%% With the unknown Method we block a name, but we risk this for
%% an easier implementation
start() -> register(msg,spawn(fun() -> cmd_loop(unknown,unknown,dict:new(),dict:new()) end)).

cmd_loop(LastPlanet,LastShip,Planets,Ships) ->
	receive
		{newS,Name,PID} -> cmd_loop(LastPlanet,PID,Planets,dict:append(Name,PID,Ships));
		{newP,Name,PID} -> cmd_loop(PID,LastShip,dict:append(Name,PID,Planets),Ships);
		{connect,Planet} -> 
			if LastPlanet /= unknown -> planet:connect(LastPlanet,dict:fetch(Planet,Planets))end,
			cmd_loop(LastPlanet,LastShip,Planets,Ships);

		{connect,Planet1,Planet2} -> 
			planet:connect(dict:fetch(Planet1,Planets),dict:fetch(Planet2,Planets)),
			cmd_loop(Planet1,LastShip,Planets,Ships);
		peers -> 
			if LastPlanet /= unknown -> planet:peers(LastPlanet) end,
			cmd_loop(LastPlanet,LastShip,Planets,Ships);
		{peers,Planet} -> 
			planet:peers(dict:fetch(Planet)),
			cmd_loop(Planet,LastShip,Planets,Ships);
		{dock,Ship,Planet} -> 
			ship:dock(dict:fetch(Ship,Ships),dict:fetch(Planet,Planets)),
			cmd_loop(Planet,Ship,Planets,Ships);
		{travel,Planet} -> 
			if LastShip /= unkown -> planet:travel(LastShip, dict:fetch(Planet,Planets))end,
			cmd_loop(Planet,LastShip,Planets,Ships);
		{travel,Ship,Planet} -> 
			planet:travel(dict:fetch(Ship,Ships),dict:fetch(Planet,Planets)),
			cmd_loop(Planet,Ship,Planets,Ships);

		test -> io:format("Hello~n");

		_ -> io:format("~w~n~w~n”",["Unbekannte Nachrich erhalten.","Droppe diese."]),
			cmd_loop(LastPlanet,LastShip,Planets,Ships)
	end.

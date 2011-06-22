-module(planet).
-export([start/0, connect/2, peers/1]).

%%
%%Sends a message to a planet, that it should connect to another planet
%% (if the other planet is located at another planet
%%it will be neccessary to enter a full qualifyed name)
%%
%% selfId - referes to the node the connect is send from
%% otherId - refers to the node that will be asked for connection
connect(SelfId,OtherId) -> otherId!{cmd_hello, SelfId}.

%%
%%Sends a message to the planet, that it should collect all peers reachable.
%%
%% From - refers the planet that should collect the data
%%
peers(From)-> From!cmd_peers.

%%
%% Starts a new planet, that can be reached through messages
%%
start() -> spawn(Fun() -> planet_loop([],dict:new(),[]) end).

planet_loop(Connected,Peers,Ships) ->
	receive
		{cmd_hello, PID} ->
			if not lists:member(PID, Connected) -> 
				PID!{hello,self()},
				planet_loop([PID|Connected],Peers,Ship) 
			end;
		cmd_peers ->
			lists:foreach(Fun(PID) -> PID!{peers,[self(),PID]} end,Connected),
			planet_loop(Connected,dict:new(),Ships);

		{hello,PID} -> 
			PID!{olleh,self()},
			if 
			not lists:member(PID, Connected) -> planet_loop([PID|Connected],Peers,Ship);
			true -> planet_loop(Connected,Peers,Ship)
			end;
		{olleh,PID} -> 
			if not lists:member(PID, Connected) -> planet:loop([PID|Connected],Peers,Ship)
			end;
		
		{peers,Way} -> help_peers(Way,Connected), planet_loop(Connected, Peers, Ships);
		{sreep,Way,New} -> 
			NewPeers = dict:merge(Fun(Key, Value1, Value2) ->
				if 
				length(Value1)>length(Value2) -> Value2;
				true -> Value1
				end end,Peers, help_sreep(Way,New,Peers)), 
			planet_loop(Connected, NewPeers, Ships);

		{dock,PID} -> 
			PID!{kcod,self()},
			planet_loop(Connected, Peers, [PID|Ships]);
		{undock,PID} -> planet_loop(Connected, Peers, lists:delete(PID,Ships))
	end.

%% --------------------- Helpfunctions -------------------------------

%%
%% Schickt die Nachricht weiter, wenn wir nicht der letzte Knoten sind.
%%
%% Sonst schickt sie uns die Connected Peers zurück.
%%
%% Verändert nichts am Zustand des Planeten (=> void function)
%%
help_peers(Way, Connected) -> 
	if 
	%% Wenn wir der letzte Knoten sind:
	lists:last(Way) == self() ->
		[We,Next|Rest] = lists:reverse(Way),
		We!{sreep,[We,Next|Rest],Connected};
	
	%% Wenn wir uns auf dem Weg befinden
	lists:member(Way,self()) ->
		{First,[We,Next|Rest]} = splitwith(Fun(PID) -> PID /= self() end, Way),
		Next!{peers,First++[We,Next|Rest]};

	%%Sollten wir nicht auf dem Weg liegen -> drop
	true -> ok
	end.

help_sreep(Way, New, Peers) ->
	if
	%% Wir haben nach peers gefragt:
	lists:last(Way) == self() ->
		lists:foldr(Fun(PID, Dict) -> dict:append(PID,lists:reverse([PID|Way]),Dict) end,dict:new(),Peers);

	%% Wir müssen die Nachricht weiterleiten
	lists:member(Way,self()) ->
		{First, [We,Next|Rest]} = splitwith(Fun(PID) -> PID /= self() end, Way),
		Next!{sreep,First++[We,Next|Rest],Peers};
	true -> ok
	end.

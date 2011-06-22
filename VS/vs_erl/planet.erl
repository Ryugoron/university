-module(planet).
-export([start/0, connect/2, peers/1]).

%%
%%Sends a message to a planet, that it should connect to another planet
%% (if the other planet is located at another planet
%%it will be neccessary to enter a full qualifyed name)
%%
%% selfId - referes to the node the connect is send from
%% otherId - refers to the node that will be asked for connection
connect(SelfId,OtherId) -> OtherId!{cmd_hello, SelfId}.

%%
%%Sends a message to the planet, that it should collect all peers reachable.
%%
%% From - refers the planet that should collect the data
%%
peers(From)-> From!cmd_peers.

%%
%% Starts a new planet, that can be reached through messages
%%
start() -> 
	spawn((fun() -> planet_loop([],dict:new(),[]) end)).

planet_loop(Connected,Peers,Ships) ->
	receive
		{cmd_hello, PID} ->
			IsElem = lists:member(PID,Connected),
			if not IsElem -> 
				PID!{hello,self()},
				planet_loop([PID|Connected],Peers,Ships) 
			end;
		cmd_peers ->
			lists:foreach(fun(PID) -> PID!{peers,[self(),PID]} end,Connected),
			planet_loop(Connected,dict:new(),Ships);

		{hello,PID} -> 
			PID!{olleh,self()},
			IsElem = lists:meber(PID, Connected),
			if 
			not IsElem -> planet_loop([PID|Connected],Peers,Ships);
			true -> planet_loop(Connected,Peers,Ships)
			end;
		{olleh,PID} ->
			IsElem = lists:member(PID, Connected), 
			if not IsElem -> planet:loop([PID|Connected],Peers,Ships)
			end;
		
		{peers,Way} -> help_peers(Way,Connected), planet_loop(Connected, Peers, Ships);
		{sreep,Way,New} -> 
			NewPeers = dict:merge(fun(_, Value1, Value2) ->
				if 
				length(Value1)>length(Value2) -> Value2;
				true -> Value1
				end end,Peers, help_sreep(Way,New)), 
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
	LastNode = lists:last(Way),
	IsElem = lists:member(Way,self()), 
	if 
	%% Wenn wir der letzte Knoten sind:
	LastNode == self() ->
		[We,Next|Rest] = lists:reverse(Way),
		We!{sreep,[We,Next|Rest],Connected};
	
	%% Wenn wir uns auf dem Weg befinden
	IsElem ->
		{First,[We,Next|Rest]} = lists:splitwith(fun(PID) -> PID /= self() end, Way),
		Next!{peers,First++[We,Next|Rest]};

	%%Sollten wir nicht auf dem Weg liegen -> drop
	true -> ok
	end.

help_sreep(Way, New) ->
	LastNode = lists:last(Way),
	IsElem = lists:member(Way,self()),
	if
	%% Wir haben nach peers gefragt:
	LastNode == self() ->
		lists:foldr(fun(PID, Dict) -> dict:append(PID,lists:reverse([PID|Way]),Dict) end,dict:new(),New);

	%% Wir müssen die Nachricht weiterleiten
	IsElem ->
		{First, [We,Next|Rest]} = lists:splitwith(fun(PID) -> PID /= self() end, Way),
		Next!{sreep,First++[We,Next|Rest],New};
	true -> ok
	end.

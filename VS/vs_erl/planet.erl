-module(planet).
-export([start/1, connect/2, peers/1, planet_loop/4]).

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
start(Name) -> spawn((fun() -> planet_loop(Name,[],dict:new(),[]) end)).

planet_loop(MyName,Connected,Peers,Ships) ->
	receive
		{cmd_hello, PID, Name} ->
			IsElem = lists:member({PID,Name},Connected),
			if not IsElem -> 
				PID!{hello,self(), MyName},
				planet_loop(MyName,[{PID,Name}|Connected],Peers,Ships) 
			end;
		cmd_peers ->
			StartDict = lists:foldr(fun({PID,Name},Dict) -> dict:append(PID,{Name,[self(),PID]},Dict) end,
				dict:new(),Connected),
			lists:foreach(fun({PID,_}) -> PID!{peers,[self(),PID]} end,Connected),
			planet_loop(MyName,Connected,StartDict,Ships);

		{hello,PID,Name} -> 
			PID!{olleh,self(),MyName},
			IsElem = lists:meber({PID,Name}, Connected),
			if 
			not IsElem -> 
				io:format("The planet ~w discovered a new planet ~w.~n",[MyName,Name]),
				msg!{newP,Name,PID},
				planet_loop(MyName,[{PID,Name}|Connected],Peers,Ships);
			true -> planet_loop(MyName,Connected,Peers,Ships)
			end;
		{olleh,PID,Name} ->
			IsElem = lists:member({PID,Name}, Connected), 
			if 
			not IsElem -> 
				io:format("The planet ~w discoverd a new planet ~w.~n",[MyName,Name]),
				msg!{newP,Name,PID},
				planet_loop(MyName,[{PID,Name}|Connected],Peers,Ships);
			true -> planet_loop(MyName,Connected,Peers,Ships)
			end;
		
		{peers,Way} -> help_peers(Way,Connected), planet_loop(MyName,Connected, Peers, Ships);
		{sreep,Way,New} -> 
			%%Wir ermitteln die neuen Knoten, packen Sie in die Liste unserer Knoten rein
			NewPeers = dict:merge(fun(_, {Name1,Value1}, {Name2,Value2}) ->
				if 
				length(Value1)>length(Value2) -> {Name2,Value2};
				true -> {Name1,Value1}
				end end,Peers, help_sreep(Way,New)),
			%%Ermitteln diejenigen die echt neu sind.
			PeersToSend = lists:foldr(fun(Key,AccPeers) -> dict:erase(Key,AccPeers) end,
				NewPeers,dict:fetch_keys(NewPeers)),
			%%Und schicken diesen eine peers Nachricht
			dict:map(fun(_,{Name,[A,B|Rest]}) -> 
				io:format("Planet ~w erreicht: ~w~n",[MyName,Name]),
				A!{peers,[A,B|Rest]}
				end,PeersToSend),
			planet_loop(MyName,Connected, NewPeers, Ships);

		{dock,PID,Name} -> 
			PID!{kcod,self(),MyName},
			io:format("On planet ~w the ship ~w entered the Orbit.~n",[MyName,Name]),
			planet_loop(MyName,Connected, Peers, [{PID,Name}|Ships]);
		{undock,PID} -> 
			io:format("On planet ~w the ship ~w left the Orbit",[MyName,lists:keyfind(PID,1,Ships)]),
			planet_loop(MyName,Connected, Peers, lists:keydelete(PID,1,Ships));

		_ -> io:format("~w~n~w~n",["Unbeannt Nachricht erhalten","Droppe Sie."]),
			planet_loop(MyName,Connected,Peers,Ships)
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
		lists:foldr(fun({PID,Name}, Dict) -> dict:append(PID,{Name,lists:reverse([PID|Way]),Dict}) end,
				dict:new(),New);

	%% Wir müssen die Nachricht weiterleiten
	IsElem ->
		{First, [We,Next|Rest]} = lists:splitwith(fun(PID) -> PID /= self() end, Way),
		Next!{sreep,First++[We,Next|Rest],New};
	true -> ok
	end.

package game.help;

import game.networking.GameMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Market {
	private Map<String, Integer> value = new HashMap<String, Integer>();
	private Map<String, Integer> need = new HashMap<String, Integer>();
	private Map<String, Integer> capa = new HashMap<String, Integer>();
	private Map<String, Integer> ttl = new HashMap<String, Integer>();

	public Market(){
	}
	
	//Erzeugt der einfachkeit halber schoneinmal ein paar Waren
	public Market(int i){
		this.newGood(GameMessage.prepareProtokoll("Deuterium"), 10, 100, 20);
		this.setGoodAmount(GameMessage.prepareProtokoll("Deuterium"), 10);
		
		this.newGood(GameMessage.prepareProtokoll("Goldgepresstes Latinum"), 10, 100, 20);
		this.setGoodAmount(GameMessage.prepareProtokoll("Goldgepresstes Latinum"), 10);
		
		this.newGood(GameMessage.prepareProtokoll("Deine Mudda"), 300, 10, 100);
		this.setGoodAmount(GameMessage.prepareProtokoll("Deine Mudda"), 5);
		
	}
	
	public void newGood(String name, int value, int need, int ttl) {
		this.value.put(name, value);
		this.need.put(name, need);
		this.ttl.put(name, ttl);
		this.capa.put(name, 0);
	}

	public void delGood(String name) {
		this.value.remove(name);
		this.need.remove(name);
		this.ttl.remove(name);
		this.capa.remove(name);
	}

	public boolean isGood(String name) {
		return this.value.containsKey(name);
	}

	public int price(String name) {
		if(this.capa.get(name) == 0) return this.value.get(name) * this.need.get(name) * 2;
		return this.value.get(name)
				* (this.need.get(name) / this.capa.get(name));
	}
	
	public int amount(String name){
		return this.capa.get(name);
	}
	
	public int ttl(String name){
		return this.ttl.get(name);
	}

	public Set<String> allGoods() {
		return this.value.keySet();
	}

	public int buy(String name, int amount) {
		if(this.capa.get(name) <= 0) return 0;
		int price = this.price(name);
		int realAmount = this.capa.get(name) < amount ? this.capa.get(name) : amount;
		this.capa.put(name, this.capa.get(name)-realAmount);
		
		return price*realAmount + 1;
	}
	
	public int sell(String name, int amount) {
		if(this.need.get(name) <= 0) return 0;
		int price = this.price(name);
		this.capa.put(name, this.capa.get(name) + amount);
		
		return price*amount + 1;
	}
	
	public void setGoodAmount(String name, int amount){
		this.capa.put(name, amount);
	}
}

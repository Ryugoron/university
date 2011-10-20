import game.planet.Planet;
import game.ship.Ship;

public class Space {
	public static void main(String[] args) {
		try {
			if (args.length == 3) {
				if (args[0].equals("planet")) {
					new Planet(Integer.parseInt(args[1]), args[2]);
				} else if(args[0].equals("ship")) {
					new Ship(Integer.parseInt(args[1]), args[2]);
				}else {
					System.out.println(help());
				}
			} else if (args.length == 2) {
				if (args[0].equals("planet")) {
					new Planet(Integer.parseInt(args[1]));
				}else if(args[0].equals("ship")) {
					new Ship(Integer.parseInt(args[1]));
				} else {
					System.out.println(help());
				}
			} else {
				new Planet(4711, "Erde");
				new Planet(4712, "Beta - Z");
				new Planet(4713, "Vulcan");
				new Planet(4714, "Praxis");
				new Planet(4715, "Romulus");
				new Ship(4716, "Enterprise");
				new Ship(4717, "Voyager");
				new Ship(4718, "Monmotma");
			}
		} catch (Exception e) {
			System.out.println(help());
		}
	}

	private static String help() {
		return  "Usage : space [Type , Port [,Name]]\n" +
				"        Type : planet, ship\n" +
				"        Port : a free Port\n" +
				"        Name : name for the typ of Object\n\n" +
				"The name is optional, but the Game will ask for a\n" +
				"        name on start.";
	}
}

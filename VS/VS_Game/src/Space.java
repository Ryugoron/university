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
				new Ship(4714, "Enterprise");
				new Ship(4715, "Voyager");
//				new Planet(4713, "Praxis");
//				new Planet(4714, "Beta - Z");
//				new Planet(4715, "Romulus");
			}
		} catch (Exception e) {
			System.out.println(help());
		}
	}

	private static String help() {
		return  "Usage : SpaceBWL [Type , Port [,Name]]\n" +
				"        Type : planet [work in Progress]\n" +
				"        Port : a free Port\n" +
				"        Name : name for the typ of Object\n\n" +
				"If no input is given an Example will be given.\n\n" +
				"The name is optional, but the Game will ask for a\n" +
				"        name on start.";
	}
}

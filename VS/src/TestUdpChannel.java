import java.net.InetAddress;
import java.net.UnknownHostException;

import vsFramework.Message;

public class TestUdpChannel {
	public static void main(String[] args) {
		try {
			UdpChannel channel1 = UdpChannelFactory.newUdpChannel(4712);
			UdpChannel channel2 = UdpChannelFactory.newUdpChannel(8889,InetAddress.getByName("127.0.0.1"), 4712);
			
			System.out.println("ich lebe noch");
			Message m = new UDPMessage("abc".getBytes());
			System.out.println("Nachricht: " + new String(m.getData()));
			channel2.send(m);
			Message m2 =channel1.recv();
			System.out.println(new String(m2.getData()));
			channel1.close();
			channel2.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

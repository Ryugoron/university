package vsFramework;

import java.util.Arrays;

public class ByteArrayMessage extends AbstractMessage {
	
	public ByteArrayMessage(byte[] data, int len) {
		this.data = Arrays.copyOf(data, len);
	}
	
	public ByteArrayMessage(byte[] data) {
		this.data = Arrays.copyOf(data, data.length);
	}

}

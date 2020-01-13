import java.util.logging.LogManager;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import com.fazecast.jSerialComm.SerialPort;

public class Main extends Thread implements NativeMouseInputListener {

	static boolean RMB;
	static boolean LMB;
	static boolean EMB;
	static boolean flag = false;
	static byte[] B0 = { 100 };
	static byte[] B1 = { 111 };

	public void nativeMouseClicked(NativeMouseEvent e) {

	}

	public void nativeMousePressed(NativeMouseEvent e) {
		if (e.getButton() == 1) {
			LMB = true;
		} else if (e.getButton() == 2) {
			RMB = true;
		}
	}

	public void nativeMouseReleased(NativeMouseEvent e) {
		if (e.getButton() == 1) {
			LMB = false;
		} else if (e.getButton() == 2) {
			RMB = false;
		}
	}

	public void nativeMouseMoved(NativeMouseEvent e) {

	}

	public void nativeMouseDragged(NativeMouseEvent e) {

	}

	SerialPort serialPort;
	boolean portFound = false;

	void sendByte(byte b) {
		byte[] buff = new byte[0];
		buff[0] = b;
		serialPort.writeBytes(buff, 1);
	}

	void startSerial() {

		// ポート名
		String portName = "COM6";

		for (SerialPort sp : SerialPort.getCommPorts()) {
			if (sp.getSystemPortName().equals(portName)) {
				serialPort = sp;
				break;
			}
		}
		// ボーレート
		//setComPortParameters​(9600, int newDataBits, int newStopBits, int newParity);
		serialPort.openPort();
	}

	public static void main(String[] args) {
		SerialPort comPort = SerialPort.getCommPorts()[2];
		comPort.openPort();
		System.out.println(comPort.getSystemPortName());
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);

		comPort.writeBytes(B0, 1);
		System.out.println(B0[0]);
		comPort.writeBytes(B1, 1);
		System.out.println(B1[0]);

		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}

		// Construct the example object.
		//System.out.println("aaaa");
		Main example = new Main() {
			@Override
			public void run() {
				System.out.println(flag);
				while (true) {
					System.out.print("");
					if (RMB && LMB&&!flag) {
						comPort.writeBytes(B0, 1);
						flag = true;
						System.out.println(flag);
					}else if (!LMB &&flag) {
						comPort.writeBytes(B1, 1);
						flag = false;
						System.out.println(flag);
					}
				}
			}
		};
		LogManager.getLogManager().reset();
		example.start();

		// Add the appropriate listeners.
		GlobalScreen.addNativeMouseListener(example);

		//GlobalScreen.addNativeMouseMotionListener(example);
		//System.out.println("cccc");

	}
}
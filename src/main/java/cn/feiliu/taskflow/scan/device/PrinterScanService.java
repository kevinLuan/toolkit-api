package cn.feiliu.taskflow.scan.device;

import com.google.common.primitives.Ints;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

/**
 * 扫描打印机设备
 *
 * @author shoushen.luan
 * @since 2022-11-15
 */
@Slf4j
public class PrinterScanService implements Closeable {
    private final String netAddress = "255.255.255.255";
    private final int PORT = 3000;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private final Map<String, PrinterDevice> printerDeviceMap = new ConcurrentHashMap<>();
    private final byte[] SEND_DATA = Ints.toByteArray(Integer.parseInt("425b5d47", 16));

    public PrinterScanService() throws SocketException {
        datagramSocket = new DatagramSocket();
        executorService.execute(() -> {
            try {
                for (; ; ) {
                    byte[] receiveBuff = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuff, receiveBuff.length);
                    datagramSocket.receive(receivePacket);
                    String receiveRawData = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    PrinterDevice device = parseDevice(receiveRawData);
                    if (device.isPrinterDevice()) {
                        printerDeviceMap.put(device.getMac(), device);
                    } else {
                        log.info("ignore datagramPacket:`{}`", receiveRawData);
                    }
                }
            } catch (SocketException e) {
            } catch (IOException e) {
                log.error("receive datagramPacket error", e);
            }
        });
    }

    private PrinterDevice parseDevice(String packInfo) {
        String[] args = packInfo.split(";");
        PrinterDevice device = new PrinterDevice();
        for (String arg : args) {
            int index = arg.indexOf(":");
            if (index > 0) {
                String name = arg.substring(0, index);
                String value = arg.substring(index + 1);
                device.addInfo(name, value);
            }

        }
        return device;
    }

    @Override
    public void close() {
        if (datagramSocket != null) {
            datagramSocket.close();
        }
        executorService.shutdown();
    }

    /***
     * 获取打印机
     * @throws IOException
     */
    public Collection<PrinterDevice> getPrinters() throws IOException {
        return printerDeviceMap.values();
    }

    /**
     * 发送数据报文
     *
     * @throws IOException
     */
    private void doSendDatagram() throws IOException {
        InetAddress address = InetAddress.getByName(netAddress);
        datagramPacket = new DatagramPacket(SEND_DATA, SEND_DATA.length, address, PORT);
        datagramSocket.send(datagramPacket);
    }

    /**
     * 发送数据报文
     *
     * @throws IOException
     */
    public void sendDatagram() throws IOException, InterruptedException {
        for (int i = 0; i < 5; i++) {
            this.doSendDatagram();
            Thread.sleep(10);
        }
    }

}

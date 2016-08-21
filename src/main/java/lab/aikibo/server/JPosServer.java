package lab.aikibo.server;

import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ServerChannel;
import org.jpos.iso.ISOServer;
import org.jpos.iso.ISOSource;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.BaseChannel;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.GenericPackager;

import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JPosServer implements ISORequestListener {
  public static void main(String args[]) throws ISOException {
    String hostname = "localhost";
    int portNumber = 12345;

    ISOPackager packager = new GenericPackager("packager/iso93ascii.xml");
    ServerChannel channel = new ASCIIChannel(hostname, portNumber, packager);
    ISOServer server = new ISOServer(portNumber, channel, null);
    server.addISORequestListener(new JPosServer());

    new Thread(server).start();

    System.out.println("Server siap menerima koneksi pada port [" + portNumber + "]");
  }

  public boolean process(ISOSource isoSrc, ISOMsg isoMsg) {
    try {
      System.out.println("Server menerima koneksi dari [" + ((BaseChannel)isoSrc).getSocket().getInetAddress().getHostAddress() + "]");
      if(isoMsg.getMTI().equalsIgnoreCase("1800")) {
        acceptNetworkMsg(isoSrc, isoMsg);
      }
    } catch(IOException ioe) {
      Logger.getLogger(JPosServer.class.getName()).log(Level.SEVERE, null, ioe);
    } catch(ISOException ex) {
      Logger.getLogger(JPosServer.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  private void acceptNetworkMsg(ISOSource isoSrc, ISOMsg isoMsg) throws ISOException, IOException {
    System.out.println("Accepting Network Management Request");
    ISOMsg reply = (ISOMsg) isoMsg.clone();
    reply.setMTI("1810");
    reply.set(39, "00");

    isoSrc.send(reply);
  }
}

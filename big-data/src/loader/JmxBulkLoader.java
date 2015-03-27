package loader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.cassandra.service.StorageServiceMBean;

public class JmxBulkLoader {
	private JMXConnector connector;
	private StorageServiceMBean storageBean;

	public JmxBulkLoader(String host, int port) throws Exception {
		connect(host, port);
	}

	private void connect(String host, int port) throws IOException,
			MalformedObjectNameException {
		JMXServiceURL jmxUrl = new JMXServiceURL(String.format(
				"service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", host, port));
		Map<String, Object> env = new HashMap<String, Object>();
		connector = JMXConnectorFactory.connect(jmxUrl, env);
		MBeanServerConnection mbeanServerConn = connector
				.getMBeanServerConnection();
		ObjectName name = new ObjectName(
				"org.apache.cassandra.db:type=StorageService");
		storageBean = JMX.newMBeanProxy(mbeanServerConn, name,
				StorageServiceMBean.class);
	}

	public void close() throws IOException {
		connector.close();
	}

	public void bulkLoad(String path) {
		long start = System.currentTimeMillis();
		storageBean.bulkLoad(path);
		
		
		long end = System.currentTimeMillis();
		System.out.println("load took " + (end - start)/1000 + " seconds");
		try {
			this.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			throw new IllegalArgumentException("usage: paths to bulk files");
		}
		JmxBulkLoader np = new JmxBulkLoader("localhost", 7199);
		for (String arg : args) {
			np.bulkLoad(arg);
		}
		np.close();
	}
}
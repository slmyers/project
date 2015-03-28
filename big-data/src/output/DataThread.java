package output;


import java.io.File;
import java.util.UUID;

import loader.JmxBulkLoader;
import cass.Application;
import cass.SSTwriter;



public class DataThread implements Runnable{
	private String threadName;
	private String folder;
	public DataThread(String threadName, String folder){
		this.threadName = threadName;
		this.folder = folder;
	}
	
	public void run(){
		final SSTwriter writer = new SSTwriter(Application.NUM_ROWS, folder); 
		
		File file = new File(folder);
		final String path = file.getAbsolutePath();
		writer.execute();
		writer.close();
		
		try {
			JmxBulkLoader jmxLoader = new JmxBulkLoader("localhost", 7199);
			jmxLoader.bulkLoad(path);
			jmxLoader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void printInfo(){
		
	}
	

	
	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
}
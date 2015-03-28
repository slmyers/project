package output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import loader.JmxBulkLoader;
import cass.Application;
import cass.SSTwriter;
import cass.Setup;



public class Output {
	public static Application app;
	
	public static void main(String[] args) throws Exception {
		String type;
		if(args.length != 1){
			System.out.println("incorrect usage");
			System.out.println("correct usage:");
			System.out.println("java -jar DatabaseGenerator-1.0-SNAPSHOT.one-jar.jar <type>");
		}
		type = args[0];
		initApp(type);
		Thread tt = new Thread(){
			public void run(){
				Setup setup = new Setup(Application.RUN_TYPE);
				setup.execute();
			}
		};
		tt.start();
		try {
			tt.join();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		long start = System.currentTimeMillis();
		run();
		long end = System.currentTimeMillis();
		PrintWriter out = new PrintWriter("log.txt");
		out.println("took " +  (end - start)/1000 + " seconds.");
		out.close();
		System.out.println("took " + (end-start)/1000 + " seconds.");
		System.exit(0);
	}

	public static void initApp(String type){
		app = Application.getApp();
		app.setTableDesc();
		app.setCreateStmnt();
		app.buildQuestionString();
		app.setType(type);
		app.createOutputDir();
	}
	
	public static void run() throws Exception{
		String type = Application.RUN_TYPE;
		int iterations = 0;
		if(type.equals("test")){
			iterations = 2;
		}else if(type.equals("demo")){
			iterations = 1000;
		}else if(type.equals("project")){
			iterations = 10000;
		}else{
			throw new RuntimeException("Appplication Run Type is not properly defined");
		}
		final SSTwriter writer = new SSTwriter(Application.NUM_ROWS); 
		File file = new File(Application.PATH_TO_DATA);
		final String path = file.getAbsolutePath();
		JmxBulkLoader jmxLoader = new JmxBulkLoader("localhost", 7199);
		
		for(int i = 0; i < iterations; i++){
			writer.execute();
			jmxLoader.bulkLoad(path);
			// check to see if this kills folder too
			purgeDirectory(Application.PATH_TO_DATA);
		}
		writer.close();
		jmxLoader.close();
	}
	
	
	private static void purgeDirectory(String dataFolder) {
		FileUtils.deleteDir(dataFolder);
	}

	public static void printStartMsg() {
		
	}

	/*
	 * long size = FileUtils.sizeOfDirectory(new File("C:/Windows/folder"));
	 * 
	 * System.out.println("Folder Size: " + size + " bytes");
	 */
	public static void printEndMsg(long totalTime) {
		
	}

	public static long folderSize(File directory) {
		long length = 0;
		for (File file : directory.listFiles()) {
			if (file.isFile())
				length += file.length();
			else
				length += folderSize(file);
		}
		return length;
	}
}

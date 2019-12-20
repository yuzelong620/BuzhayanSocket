package xlcr_app_server;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.xlcr.thread.WorkThread;

public class test {

	 public static void main(String[] args) throws InterruptedException, ExecutionException {
		 FutureTask<String> furure=new FutureTask<>(new Callable<String>() {
				public String call() throws Exception {
					 return handlerMessage();
				}
			});
			
			
			WorkThread work=new WorkThread("work thread");
			work.start();
			work.put(furure);
			furure.get();
			System.out.println("执行完了");
	 }
	 
	 
	 
	 
		static String handlerMessage() {
			try {
				System.out.println("开始逻辑");
				Thread.sleep(3000);
				System.out.println("结束逻辑");
				
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		
	}
	 

}

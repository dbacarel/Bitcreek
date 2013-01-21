

import java.util.concurrent.*;

public class Thread_Manager  {

	static ExecutorService pool_down;
	static ExecutorService pool_share;
	static ExecutorService pool_tools;


	public Thread_Manager(){
		pool_down=Executors.newCachedThreadPool();
		pool_share=Executors.newCachedThreadPool();
		pool_tools=Executors.newCachedThreadPool();
		
	}
	
	public void aggiungi_share(Runnable nuovo){
		pool_share.execute(nuovo);
				
	}
	
	public void aggiungi_down(Runnable nuovo){
			
		pool_down.execute(nuovo);
				
	}
	
	public void aggiungi_tool(Runnable nuovo){
		
		pool_tools.execute(nuovo);
				
	}
	
}

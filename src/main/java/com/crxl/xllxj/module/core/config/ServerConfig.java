package com.crxl.xllxj.module.core.config;

public class ServerConfig{
	
	private int thread_max_task_size = 500;//主线程最大任务数量
	private int server_port;
    private boolean ssl_open;
    private String  ssl_password;
    private String  ssl_filename;
    private String  ssl_type;
    private int     jetty_port=80;
    private String gamePath;
    private String  main_server_address;//主服务器地址--如果这个值为空，那么当前服务器就是主服务器
    
    /**
     * 判断是否
     * @return
     */
    public boolean isMainServer(){
    	return main_server_address==null;
    }
    ///

	public int getServer_port() {
		return server_port;
	}

	public void setServer_port(int server_port) {
		this.server_port = server_port;
	}

	public boolean isSsl_open() {
		return ssl_open;
	}

	public void setSsl_open(boolean ssl_open) {
		this.ssl_open = ssl_open;
	}

	public String getSsl_password() {
		return ssl_password;
	}

	public void setSsl_password(String ssl_password) {
		this.ssl_password = ssl_password;
	}

	public String getSsl_filename() {
		return ssl_filename;
	}

	public void setSsl_filename(String ssl_filename) {
		this.ssl_filename = ssl_filename;
	}

	public String getSsl_type() {
		return ssl_type;
	}

	public void setSsl_type(String ssl_type) {
		this.ssl_type = ssl_type;
	}

	public int getJetty_port() {
		return jetty_port;
	}

	public void setJetty_port(int jetty_port) {
		this.jetty_port = jetty_port;
	}

	public String getMain_server_address() {
		return main_server_address;
	}

	public void setMain_server_address(String main_server_address) {
		this.main_server_address = main_server_address;
	}

	public int getThread_max_task_size() { 
		return thread_max_task_size;
	}

	public void setThread_max_task_size(int main_max_task_size) {
		this.thread_max_task_size = main_max_task_size;
	}

	public String getGamePath() {
		return gamePath;
	}

	public void setGamePath(String gamePath) {
		this.gamePath = gamePath;
	}

	 
	
    
	  
}

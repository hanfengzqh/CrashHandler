#项目描述
##一、项目完成Android工程全局异常的捕获以及异常Log存储功能；
##二、实现Log上传指定服务器功能，通过过程采用DES(CBC)加密处理，一定程度上保证了数据传输的安全性。
##三、实现以文件形式上传指定服务器以及文件弄容方式上传之指定服务器。
##四、从数据库读取server地址，便于动态配置请求地址。
##五、可以将项目封装为jar包放入工程当中使用。
##六、添加外部事件调用接口:
    实现MyCrashHandler.OutEventExec接口
    在Application中实现:
    MyCrashHandler crashHandler = MyCrashHandler.getInstance();
    		       crashHandler.init(this);
    		       crashHandler.setOutEventExec(this);
    @Override
    	public void outEventExec() {
    		Log.d("zqh", "outEventExec 执行 ");
    	}

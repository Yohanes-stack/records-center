# 第七篇 开始与结束
万事万物都要有始有终，bean也不例外。因此本篇主要是完成bean的初始化和销毁
在xml文件中制定init-method和destroy-method 即可在创建和销毁时进行方法调用
为了确保销毁方法在虚拟机关闭之前，需要在虚拟机注册一个钩子方法
AbstractApplicationContext#registerShutdownHook
![img.png](img.png)
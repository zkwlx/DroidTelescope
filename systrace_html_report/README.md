当方法量很大时，SysTrace 生成的文件过大，比方便定位问题，于是杨帆同学开发了 Systrace 报告的剪裁工具 systrace_html_reduce.py，工具会删除下列无用内容：非主动记录的耗时，非 ui 线程耗时。
用法是：python3 systrace_html_reduce.py trace.html out.html，

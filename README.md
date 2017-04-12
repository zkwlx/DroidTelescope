# AndrPerfMonitor
这是一套Android端线上应用性能监控框架，目前支持卡顿监控、内存泄露监控；后续还会增加更多监控对象。此项目部分功能参考自[BlockCanaryEx](https://github.com/seiginonakama/BlockCanaryEx)。

##使用效果
当发生卡顿时，框架会记录相关方法的调用时间和调用栈，并生成JSON格式的日志，如下例子：

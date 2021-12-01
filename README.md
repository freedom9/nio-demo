![](https://gitee.com/freedom9/markdown_images/raw/master/java/Channel%E5%92%8CBuffer%E7%9A%84%E5%85%B3%E7%B3%BB.png)

#### 1、Channel
Channel是一个通道，可以通过它读取和写入数据，类似于水管，网络数据通过Channel读取和写入。通道与流的不同之处在于通道是双向的，流只是在一个方向上移动，而且通道可以用于读、写或者同时用于读写。

#### 2、Buffer
Buffer用于和NIO通道进行交互。数据是从通道读入缓冲区，从缓冲区写入通道中的。buffer本质上是一块可以写入数据，然后可以从中读取数据的内存。这块内存被包装成NIO Buffer对象，并提供了一组方法，用来方便的访问该块内存。缓冲区实际上是一个容器对象，更直接的说，其实就是一个数组，**在NIO库中，所有数据都是用缓冲区处理的**。

##### 2.1 Buffer读写数据四个步骤
1. 写入数据到Buffer。
2. 调用flip()方法。
3. 从Buffer中读取数据。
4. 调用clear()方法或者compact()方法。

**说明：**
>* flip()：从写模式切换到读模式。
>* clear()：清空整个缓冲区。
>* compact()：只会清除已经读过的数据。任何未读的数据都被移到缓冲区的起始处，新写入的数据将放到缓冲区未读数据的后面。

##### 2.2 capacity、position和limit
![](https://gitee.com/freedom9/markdown_images/raw/master/java/capacity%E3%80%81position%E5%92%8Climit.png)

**2.2.1 capacity**

作为一个内存块，Buffer有一个固定的大小值，也叫“capacity”。一旦Buffer满了，需要将其清空（通过读数据或者清除数据）才能继续写数据。

**2.2.2 position**
>* 写数据：position表示写入数据的当前位置，position的初始值为0。当写入一个数据写到Buffer后，position会向下移动到下一个可插入数据的Buffer单位。position最大可为capacity - 1。
>* 读数据：position表示读入数据的当前位置。通过ByteBuffer.flip()切换到读模式时position会被重置为0，当Buffer从position读入数据后，position会下移到下一个可读入的数据Buffer单位。

**2.2.3 limit**
>* 写数据：limit表示可对Buffer最多写入的个数，等价于capacity。
>* 读数据：limit表示Buffer里有多少可读入数据，因此能读到之前写入的所以的数据。

#### 3、Selector
Selector用于检查一个或多个Channel的状态是否处于可读或可写。使用Selector的好处在于：使用更少的线程来处理channel，相比使用多线程，避免线程上下文切换。并不是所有的Channel都可以被Selector复用，被复用的前提是**类是否继承抽象类SelectableChannel**。FileChannel不能被选择器复用。

#### 4、NIO编程步骤
1. 创建Selector选择器
2. 创建ServerSocketChannel通道，并绑定监听端口
3. 设置Channel通道是非阻塞模式
4. 把Channel注册到Socketor选择器山，监听连接事件
5. 调用Selector的select()方法（循环调用），监测通道的就绪状况
6. 调用selectKeys()方法获取就绪channel集合
7. 遍历就绪channel集合，判断就绪事件类型，实现具体的业务操作
8. 根据业务，决定是否需要再次注册监听事件，重复执行第三步操作

#### 5、Pipe
Java NIO管道是2个线程之间的单向数据连接。Pipe有一个source通道和一个sink通道，数据会被写到sink通道，从source通道读取。

![](https://gitee.com/freedom9/markdown_images/raw/master/java/Pipe.png)
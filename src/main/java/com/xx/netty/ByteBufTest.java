package com.xx.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * @author yifanl
 * @Date 2020/6/8 14:49
 */
public class ByteBufTest {
    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(9, 100);
        print("allocate ByteBuf(9, 100)", buffer);

        // write 方法改变写指针，写完之后写指针未到 capacity 的时候，buffer 仍然可写
        buffer.writeBytes(new byte[]{1,2,3,4});
        print("writeBytes(new byte[]{1,2,3,4})", buffer);

        // write 方法改变写指针，写完之后写指针未到 capacity 的时候，buffer 仍然可写, 写完 int 类型之后，写指针增加4
        buffer.writeInt(12);
        print("writeInt(12)", buffer);

        // write 方法改变写指针, 写完之后写指针等于 capacity 的时候，buffer 不可写
        buffer.writeBytes(new byte[]{5});
        print("writeBytes(5)", buffer);

        // write 方法改变写指针，写的时候发现 buffer 不可写则开始扩容，扩容之后 capacity 随即改变
        buffer.writeBytes(new byte[]{6});
        print("writeBytes(6)", buffer);

        // get 方法不改变读写指针
        System.out.println("getByte(3) return: " + buffer.getByte(3));
        System.out.println("getShort(3) return: " + buffer.getShort(3));
        System.out.println("getInt(3) return: " + buffer.getInt(3));
        print("getByte()", buffer);

        // set 方法不改变读写指针
        buffer.setByte(buffer.readableBytes() + 1, 0);
        print("setByte()", buffer);

        // read 方法改变读指针
        byte[] dst = new byte[buffer.readableBytes()];
        buffer.readBytes(dst);
        print("readBytes(" + dst.length + ")", buffer);



    }

    private static void print(String action, ByteBuf byteBuf){
        System.out.println("after ===========" + action + "============");
        System.out.println("byteBuf.capacity():" + byteBuf.capacity());
        System.out.println("byteBuf.maxCapacity():" + byteBuf.maxCapacity());
        System.out.println("byteBuf.readerIndex():" + byteBuf.readerIndex());
        System.out.println("byteBuf.readableBytes():" + byteBuf.readableBytes());
        System.out.println("byteBuf.isReadable():" + byteBuf.isReadable());
        System.out.println("byteBuf.writerIndex():" + byteBuf.writerIndex());
        System.out.println("byteBuf.writableBytes():" + byteBuf.writableBytes());
        System.out.println("byteBuf.isWritable():" + byteBuf.isWritable());
        System.out.println("byteBuf.maxWritableBytes():" + byteBuf.maxWritableBytes());
        System.out.println();
    }
}

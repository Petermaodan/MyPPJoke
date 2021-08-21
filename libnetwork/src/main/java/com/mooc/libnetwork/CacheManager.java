package com.mooc.libnetwork;

//网络数据的序列化和反序列化

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CacheManager {

    //反序列化，把二进制数据转换成java object对象
    private static Object toObject(byte[] data){
        ByteArrayInputStream bais=null;
        ObjectInputStream ois=null;
        try{
            bais=new ByteArrayInputStream(data);
            ois=new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            try{
                if (bais!=null){
                    bais.close();
                }
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //序列化存储数据需要转换成二进制
    private static <T> byte[] toByteArray(T body){
        ByteArrayOutputStream baos=null;
        ObjectOutputStream oos=null;
        try{
            baos=new ByteArrayOutputStream();
            oos=new ObjectOutputStream(baos);
            oos.writeObject(body);
            oos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if (baos!=null){
                    baos.close();
                }
                if (oos!=null){
                    oos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];

    }
}

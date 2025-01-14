package com.xuecheng.media;

import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class BigFileTest {


    @Test
    public void testChunk() throws IOException {
        File sourceFile = new File("D:\\ASUS\\Videos\\game.mp4");
        String chunkFilePath = "D:\\ASUS\\Videos\\chunk\\";

        //分块大小
        int chunkSize = 1024 * 1024 * 5;

        //分块个数
        int chunkNum = (int) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        //使用流从源文件读取数据，向分块文件谢书记
        RandomAccessFile raf_r = new RandomAccessFile(sourceFile, "r");

        //缓冲区
        byte[] bytes = new byte[1024];

        for (int i = 0; i < chunkNum; i++) {
            File chunkFile = new File(chunkFilePath + i);
            RandomAccessFile raf_rw = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = raf_r.read(bytes)) != -1) {
                raf_rw.write(bytes, 0, len);
                if (chunkFile.length() >= chunkSize) {
                    break;
                }
            }
            raf_rw.close();
        }
        raf_r.close();
    }

    @Test
    public void testMerge() throws IOException {
        File sourceFile = new File("D:\\ASUS\\Videos\\game.mp4");
        File chunkFolder = new File("D:\\ASUS\\Videos\\chunk");
        File mergeFile = new File("D:\\ASUS\\Videos\\merge.mp4");

        File[] files = chunkFolder.listFiles();
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });

        byte[] bytes = new byte[1024];
        RandomAccessFile raf_rw = new RandomAccessFile(mergeFile, "rw");

        for (File file : fileList) {
            RandomAccessFile raf_r = new RandomAccessFile(file, "r");
            int len = -1;
            while ((len = raf_r.read(bytes)) != -1) {
                raf_rw.write(bytes, 0, len);
            }
            raf_r.close();
        }
        raf_rw.close();

        //合并文件校验
        String mded = DigestUtils.md5DigestAsHex(new FileInputStream(mergeFile));
        String sour = DigestUtils.md5DigestAsHex(new FileInputStream(sourceFile));

        if(mded.equals(sour)){
            System.out.println("sueccess");
        }

    }


}

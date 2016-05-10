package hahn.learning.jni;

import hahn.learning.larray.LByteArray;

/**
 * Created by jianghan on 16-5-10.
 */
public class TestMyLArray {

    public static void main(String[] args) {
        byte[] data = "0123456789".getBytes();
        for (int i = 0; i < 10; i++) {
            LByteArray lba = LByteArray.newLByteArray(data);
            System.out.println(new String(lba.getBytes()));
        }
    }

}

import android.content.Context;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by IntelliJ IDEA.
 * Author:Eric
 * Date:2015/1/14
 * Desc:加密解密管理类
 * 加密算法 : 将文件的数据流的每个字节与加密解密key对应字符做异或运算.
 * 解密算法 : 已经加密的文件再执行一次对文件的数据流的每个字节与加密解密key对应字符做异或运算.
 * this method can decrypt or encrypt a large file in 100 milliseconds,just have a try and see
 */
public class FileEnDecryptManager {

    private String key = "abcdefg"; // 加密解密key(Encrypt or decrypt key)

    private FileEnDecryptManager() {
    }

    private static FileEnDecryptManager instance = null;

    public static FileEnDecryptManager getInstance(Context context) {
        synchronized (FileEnDecryptManager.class) {
            if (instance == null) {
                instance = new FileEnDecryptManager();
            }
        }
        return instance;
    }

    /**
     * 加密入口(encrypt intrance)
     *
     * @param fileUrl 文件绝对路径
     * @return
     */
    public boolean doEncrypt(String fileUrl) {
        try {
            if (isDecrypted(fileUrl)) {
                if (encrypt(fileUrl)) {
                    // 可在此处保存加密状态到数据库或文件(you can save state into db or file)
                    LogUtils.d("encrypt succeed");
                    return true;
                } else {
                    LogUtils.d("encrypt failed");
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private final int REVERSE_LENGTH = 28; // 加解密长度(Encryption length)

    /**
     * 加解密(Encrypt or deprypt method)
     *
     * @param strFile 源文件绝对路径
     * @return
     */
    private boolean encrypt(String strFile) {
        int len = REVERSE_LENGTH;
        try {
            File f = new File(strFile);
            if (f.exists()) {
                RandomAccessFile raf = new RandomAccessFile(f, "rw");
                long totalLen = raf.length();

                if (totalLen < REVERSE_LENGTH)
                    len = (int) totalLen;

                FileChannel channel = raf.getChannel();
                MappedByteBuffer buffer = channel.map(
                        FileChannel.MapMode.READ_WRITE, 0, REVERSE_LENGTH);
                byte tmp;
                for (int i = 0; i < len; ++i) {
                    byte rawByte = buffer.get(i);
                    if (i <= key.length() - 1) {
                        tmp = (byte) (rawByte ^ key.charAt(i)); // 异或运算(XOR operation)
                    } else {
                        tmp = (byte) (rawByte ^ i);
                    }
                    buffer.put(i, tmp);
                }
                buffer.force();
                buffer.clear();
                channel.close();
                raf.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 解密入口(decrypt intrance)
     *
     * @param fileUrl 源文件绝对路径
     */
    public void doDecrypt(String fileUrl) {
        try {
            if (!isDecrypted(fileUrl)) {
                decrypt(fileUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void decrypt(String fileUrl) {
        if (encrypt(fileUrl)) {
            // 可在此处保存解密状态到数据库或文件(you can save state into db or file)
            LogUtils.d("decrypt succeed");
        }
    }

    /**
     * fileName 文件是否已经解密了(Whether file has been decrypted)
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    private boolean isDecrypted(String filePath) throws IOException {
        // 从数据库或者文件中取出此路径对应的状态(get state out from db or file)
    }
}

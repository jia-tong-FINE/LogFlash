package TCFGmodel;

import java.io.File;

public class FileUtil {

    public static void CreateDir(String sp) {
        File file=new File(sp);
        if(!file.exists()){
            file.mkdir();
        }
    }
}

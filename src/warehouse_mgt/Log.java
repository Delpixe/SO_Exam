package warehouse_mgt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author m.delpapa
 */
public class Log {
    private static String path = null;
    private static boolean canWriteLog = false;
    private static boolean wannaWrite = false;

    /* write_log */
    private static final ReentrantLock write_log_lck = new ReentrantLock(true);
    private static final Semaphore write_log_sem = new Semaphore(1,true);//a guardia di write_log_lck
    /* end-write_log */


    public static void insertPath(){
        if ((path == null) & (wannaWrite)){
            SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");

            String logpath = FileSystems.getDefault().getPath("").toAbsolutePath().toString();
            logpath += "\\log\\";
            path = logpath;

            File file = new File(logpath);
            canWriteLog = (file.mkdirs() | file.exists());
            System.out.println("la path del log è " + logpath);
        }
    } //end-insertPath

    public static void writeLog(String data){
        if (canWriteLog){
            try{
                write_log_sem.acquire();
            }catch(InterruptedException e){
                System.out.println(e.toString());
            }
            write_log_lck.lock();

            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
                File f1 = new File(path +"/log_" + format.format(Calendar.getInstance().getTime()) + ".log");


                if (!f1.exists()){
                    boolean file_created = false;
                    while(!file_created) {
                        file_created = f1.createNewFile();
                    }
                }

                //FileWriter fw = new FileWriter(f1.getName(),true);
                FileWriter fw = new FileWriter(f1.getAbsoluteFile(),true);
                BufferedWriter bw = new BufferedWriter(fw);

                if (! data.isEmpty()) {
                    SimpleDateFormat lineformat = new SimpleDateFormat("[dd/MM/yyyy HH:mm:ss]: ");
                    data = lineformat.format(Calendar.getInstance().getTime()) + data;
                }

                bw.append(data);
                bw.newLine();
                bw.close();

            } catch(IOException e){
                e.printStackTrace();
            }
            write_log_lck.unlock();
            write_log_sem.release();
        }
    } //end-writeLog

    public static boolean wannaWriteLog() {
        wannaWrite = false;
        String line = "";

        while ((!line.equals("S")) & (!line.equals("N"))) {
            System.out.println("Vuoi scrivere il log? [S|N]: ");
            line = Utility.askTrueFalse();
            if ((!line.equals("S")) & (!line.equals("N")))
                System.out.println("Errore nell'inserimento, riprova!");
        }
        if (line.equals("S"))
            wannaWrite = true;
        return  wannaWrite;
    }//end-wannaWriteLog



}

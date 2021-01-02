package warehouse_mgt;

import java.util.List;

public class Warehouse_Mgt {

    public static void main(String[] args) {
        if (Log.wannaWriteLog())
            Log.insertPath();

        //Random rnd = new Random();
        //int processNo = Math.abs(rnd.nextInt(1000));
        int processNo = Utility.catchProcessToLaunch();
        int accessiTotali = Utility.catchTotalAccess();
        int memFisica = Utility.catchPhysicalMemory();

        welcome();

        //Instance of the MMU
        //MMU mmu = new MMU(memFisica, accessiTotali);

        //Instance of an array of Process
        //Processo[] processes = new Processo[processNo];

        //Instance the processes
        for (int i = 0; i < processes.length ; i++)
            processes[i] = new Processo(mmu,i);
        for (int i = 0; i < processes.length ; i++)
            processes[i].start();

        /* join del process, non serve */
        try{
            for (int i = 0; i < processes.length ; i++)
                processes[i].join();
        }catch (InterruptedException le){
            System.out.println(le.toString());
        }

        printFinalPageList();
    }

    private static void welcome() {
        System.out.println("----------------------------"); //log
        System.out.println("Inizio del programma"); //log
        System.out.println("I processi lanciati sono : " + processNo);
        System.out.println("Su " + memFisica + " di memoria");
        System.out.println("con " + accessi + " accessi da fare.");
        System.out.println("----------------------------"); //log

        Log.writeLog("main",""); //log
        Log.writeLog("main","----------------------------"); //log
        Log.writeLog("main","Inizio del programma"); //log
        Log.writeLog("main","I processi lanciati sono : " + processNo); //log
        Log.writeLog("main","Su " + memFisica + " di memoria"); //log
        Log.writeLog("main","con " + accessi + " accessi da fare.");//log
        Log.writeLog("main","----------------------------"); //log
        Log.writeLog("main",""); //log
    } //end-welcome

    private static void printFinalPageList() {

        System.out.println("________________________________________________________");
        System.out.println("Inizio Stampa Lista delle Pagine");
        System.out.println("________________________________________________________");

        waitForEnter();

        System.out.println("Totale della mmu :");
        System.out.println("-> page-hit " + mmu.getpage_hit());
        System.out.println("-> page-faults " + mmu.getpage_faults());
        System.out.println("-> page-replaces " + mmu.getpage_replaces());
        System.out.println("________________________________________________________");

        Log.writeLog("main","________________________________________________________"); //log
        Log.writeLog("main","Inizio Stampa Lista delle Pagine"); //log
        Log.writeLog("main","________________________________________________________"); //log
        Log.writeLog("main","Totale della mmu :"); //log
        Log.writeLog("main","-> page-hit " + mmu.getpage_hit()); //log
        Log.writeLog("main","-> page-faults " + mmu.getpage_faults()); //log
        Log.writeLog("main","-> page-replaces " + mmu.getpage_replaces()); //log
        Log.writeLog("main","________________________________________________________"); //log

        waitForEnter();

        System.out.println("Totale per processo :");
        Log.writeLog("main","Totale per processo :"); //log
        for (Processo process : processes) {
            process.print_processo_pagine_List(mmu);
        }
        System.out.println("________________________________________________________");
        Log.writeLog("main","________________________________________________________"); //log


        if (wannaSeePages()){
            System.out.println("Totale per pagina :");
            Log.writeLog("main","Totale per pagina :"); //log

            List<Page> tableOfPages = mmu.getTableOfPages();
            for (Page page : tableOfPages) {
                System.out.println("Pagina " + page.getId() +" [" + page.getPage_hit() + " page-hit; " + page.getPage_faults() + " page-faults]");
                Log.writeLog("main","Pagina " + page.getId() +" [" + page.getPage_hit() + " page-hit; " + page.getPage_faults() + " page-faults]");//log
            }
            System.out.println("________________________________________________________");
            Log.writeLog("main","________________________________________________________"); //log
        }

    } //end-printFinalPageList

    public static void waitForEnter() {
        System.out.println("Premi Invio per continuare...");
        Utility.ReadLine();
    }//end-waitForEnter

    public static boolean wannaSeePages() {
        String line = "";
        while ((!line.equals("S")) & (!line.equals("N"))) {
            System.out.println("Vuoi vedere lo spaccato per pagina? [S|N]: ");
            line = Utility.askTrueFalse();
            if ((!line.equals("S")) & (!line.equals("N")))
                System.out.println("Errore nell'inserimento, riprova!");
        }
        return (line.equals("S"));
    }
}

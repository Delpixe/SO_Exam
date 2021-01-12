package gestoremagazzino;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
/**
 *
 * @author m.delpapa
 */
public class Utility {
    private static int askInt(){
        int value = -1;

        // Read the line as a string
        String linea = ReadLine();
        // translate the string into int
        try{
            value = Integer.parseInt(linea);
        }catch(NumberFormatException e){
            System.out.println("Impossibile tradurre in intero: ");
            System.out.println(e.toString());
        }
        return value;
    } //end-askInt

    public static String askTrueFalse(){
        // Read the line as a string
        String linea = ReadLine();
        return linea.toUpperCase();
    } //end-askBool

    public static String ReadLine() {
        String lineReaded;
        // oggetto wrapper per rendere evoluto System.in e poter leggere un'itera linea come stringa.
        BufferedReader bRead;

        // ora inizializzo l'oggetto wrapper sopra System.in
        bRead = new BufferedReader(new InputStreamReader(System.in));
        // ora vado a leggere una linea da standard input.
        try{
            lineReaded = bRead.readLine();
        }catch(IOException e){
            System.out.println("Impossibile leggere da standard IO.");
            lineReaded = "ERRORE DI IO";
        }
        return lineReaded;
    }//end-ReadLine

    public static double calcolaDeviazioneStandard(double arrayTempi[])
    {
        double DeviazioneStandard = 0.0;
        int length = arrayTempi.length;

        double mean = calcolaMedia(arrayTempi);

        for(double numero: arrayTempi)
            DeviazioneStandard += Math.pow(numero - mean, 2);

        return Math.sqrt(DeviazioneStandard/length);
    }

    public static double calcolaMedia(double[] array) {
        double somma = 0.0;
        int length = array.length;

        for(double numero : array)
            somma += numero;

        return somma/length;
    }
    /*
    public static int catchToLaunch(){
        int value = 0;
        while (value <= 0){
            System.out.println("Inserisci quanto devo lanciare : ");
            value = askInt();
            if (value <= 0)
                System.out.println("Errore nell'inserimento, riprova!");
        }
        return value;
    }//end-catchToLaunch
     */
}

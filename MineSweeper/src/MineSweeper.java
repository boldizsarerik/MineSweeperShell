import com.sun.javafx.util.Utils;
import java.util.List;
import shell.Command;
import shell.Result;
import shell.Shell;

public class MineSweeper extends Shell{
    boolean init, vege;
    String[][] jatekter;
    int aknaDb, zaszloDb;
    String kimenetel;
    
    protected void Init() {
        super.init(); // meghívjuk a szülőjét (a szülőosztály init metódusát)
        init = true;
        jatekter = new String[11][11];
        zaszloDb = 0;
        vege = false;
        
        int i, j; jatekter[0][0] = "";
        for(i=1; i<11; ++i)
            for(j=1; j<11; ++j)
                jatekter[i][j] = ".";
        
        i=0;
        for(j=1; j<11; ++j)
            jatekter[i][j] = Character.toString((char)(j+64));
           
        j = 0;
        for(i=1; i<11; ++i)
            jatekter[i][j] = Integer.toString(i);
    }
    
    public void Print(){
        int i, j;
        for(i=0; i<11; ++i)
        {
            for(j=0; j<11; ++j)
                format("%2s", jatekter[i][j]);
            format("%n");
        }
        
        format("%d db. akna van elrejtve a játéktéren! ", aknaDb);
        format("%d db. zászló áll még rendelkezésre.%n", zaszloDb);
        if(vege == false)
            format("A játék még nem ért véget!%n");
        else format("A játék véget ért! Ön %s a játékot!%n", kimenetel);
        
    }

    public MineSweeper() {
        addCommand(new Command("new") {
            @Override
            public boolean execute(String... strings) {
                if(strings.length == 1)  
                {
                    try {
                        Integer.parseInt(strings[0]);
                    } catch (NumberFormatException e) {
                        return false;
                    } 
                    
                    if(1 <= Integer.parseInt(strings[0]) && Integer.parseInt(strings[0]) <= 99) {
                        Init();
                        aknaDb = Integer.parseInt(strings[0]);
                        zaszloDb = aknaDb;
                        Print();
                    } else return false;
                }
                else if(strings.length == 0)
                {
                    Init();
                    aknaDb = 10;
                    zaszloDb = aknaDb;
                    Print();
                }
                else return false;
                
                return true;
            }
        });
        addCommand(new Command("print") {
            @Override
            public boolean execute(String... strings) {
                if(strings.length != 0 || init == false)
                    return false; 
               
                Print();
                
                return true;
            }
        });
        addCommand(new Command("flag") {
            String oszlopcimke = "ABCDEFGHIJ";
            
            @Override
            public boolean execute(String... strings) {
                try {
                        Integer.parseInt(strings[1]);
                    } catch (Exception e) { // ha csak a NumberFormatException -t "kapom el", akkor pl. flag A3-ra kiakad a program, mivel nincs 2. argumentuma a parancsnak és nem tudja, mit alakítson intté
                        return false;
                    } 
                if(strings.length != 2 || vege == true || init == false || zaszloDb == 0 
                        || !(1 <= Integer.parseInt(strings[1]) && Integer.parseInt(strings[1]) <= 10) || !Utils.contains(oszlopcimke, strings[0])
                        || !jatekter[Integer.parseInt(strings[1])][(int)strings[0].charAt(0)-64].equals(".") )
                    return false; 
               
                jatekter[Integer.parseInt(strings[1])][(int)strings[0].charAt(0)-64] = "F";
                zaszloDb--;
                
                return true;
            }
        });
        addCommand(new Command("unflag") {
            String oszlopcimke = "ABCDEFGHIJ";
            
            @Override
            public boolean execute(String... strings) {
                try {
                        Integer.parseInt(strings[1]);
                    } catch (Exception e) {
                        return false;
                    } 
                if(strings.length != 2 || vege == true || init == false || zaszloDb == aknaDb 
                        || !(1 <= Integer.parseInt(strings[1]) && Integer.parseInt(strings[1]) <= 10) || !Utils.contains(oszlopcimke, strings[0])
                        || !jatekter[Integer.parseInt(strings[1])][(int)strings[0].charAt(0)-64].equals("F") )
                    return false; 
               
                jatekter[Integer.parseInt(strings[1])][(int)strings[0].charAt(0)-64] = ".";
                zaszloDb++;
                
                return true;
            }
        });
        addCommand(new Command("fire") {
            String oszlopcimke = "ABCDEFGHIJ";
            
            @Override
            public boolean execute(String... strings) {
                try {
                        Integer.parseInt(strings[1]);
                    } catch (Exception e) {
                        return false;
                    } 
                if(strings.length != 2 || vege == true || init == false
                        || !(1 <= Integer.parseInt(strings[1]) && Integer.parseInt(strings[1]) <= 10) || !Utils.contains(oszlopcimke, strings[0])
                        || !jatekter[Integer.parseInt(strings[1])][(int)strings[0].charAt(0)-64].equals(".") )
                    return false; 
                
                List<Result> res = resultOfShot(Integer.parseInt(strings[1])-1, (int)strings[0].charAt(0)-64 -1); // muszáj csökkentem az indexeket eggyel, másképp nem jól működik a program, mivel a Shell táblája csak 10x10-es, az indexek el vannak csúszva az enyémhez képest
                /*for(Result cella : res){
                    System.out.println("res mérete: " + res.size());
                    System.out.println("column: " + cella.getColumn());
                    System.out.println("row: " + cella.getRow());
                    System.out.println("value: " + cella.getValue());
                }*/
               
                if(res.size() == 1 && res.get(0).getValue() == 10){
                    kimenetel = "elvesztette"; vege = true; 
                }
                else for(Result cella : res){
                    if(cella.getValue() == 0)
                        jatekter[cella.getRow() +1][cella.getColumn() +1] = "";
                    else jatekter[cella.getRow() +1][cella.getColumn() +1] = Integer.toString(cella.getValue());
                }     
               
                int felderitetlenDb = 0;
                for(int i=1; i<11; ++i)
                    for(int j=1; j<11; ++j)
                        if(jatekter[i][j].equals("."))
                            felderitetlenDb++;
              
                if(aknaDb-zaszloDb + felderitetlenDb == aknaDb){
                    kimenetel = "megnyerte"; vege = true; 
                }
                    
                
                Print();
                return true;
            }
        });
        addCommand(new Command("solution") {
            @Override
            public boolean execute(String... strings) {
                if(strings.length != 0 || vege == false || init == false)
                    return false; 
               
                format("%s", solution());
                
                return true;
            }
        });
    }
    
    public static void main(String[] args) {
        Shell sh = Loader.load();
        sh.readEvalPrint();
    }
}
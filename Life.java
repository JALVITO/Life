import java.io.*;
import java.util.*;
public class Life{
  
  private static BufferedReader stdIn= new  
    BufferedReader (new InputStreamReader(System.in));  
  
  //Generation
  static int gen = 0;
  static boolean pegar = false;
  static double density = 0;
  
  //Main mats
  static boolean mat[][] = new boolean[52][52];
  static boolean matAlt[][] = new boolean[mat.length][mat[0].length];
  
  //Legacy mats
  static boolean hist[][][] = new boolean[15][mat.length][mat[0].length];
  
  //////////////////////////////////////////////////////////////////
  
  public static void fill(){
    for (int x = 0; x < mat.length; x++){
      for (int y = 0; y < mat[0].length; y++){
        mat[x][y] = new Random().nextBoolean();
      }
    }
  }
  
  //////////////////////////////////////////////////////////////////
  
  public static void mostrar(){
    for (int x = 0; x < mat.length; x++){
      for (int y = 0; y < mat[0].length; y++){
        if (mat[x][y]) System.out.print("X ");
        else System.out.print("  ");
      }
      System.out.print(" ");
      if (x == 1) {System.out.print(" Gen: " + gen);}
      if (x == 2) {System.out.print(" Density: " + (int)(density/2704 * 100) + "%");}
      System.out.println();
    }
  }
  
  //////////////////////////////////////////////////////////////////
  
  public static void mostrarDebug(){
    for (int x = 0; x < mat.length; x++){
      for (int y = 0; y < mat[0].length; y++){
        if (mat[x][y]) System.out.print("X ");
        else System.out.print("O ");
      }
      System.out.print(" ");
      if (x == 1) {System.out.print(" Gen: " + gen);}
      if (x == 2) {System.out.print(" Density: " + (int)(density/2704 * 100) + "%");}
      System.out.println();
    }
  }
  
  //////////////////////////////////////////////////////////////////
  
  public static int check(int x, int y){
    int counter = 0;
    //System.out.println(x + " " + y); //Debug
    boolean upperL = (x == 0);
    boolean bottomL = (x == mat.length - 1);
    boolean rightL = (y == mat.length - 1);
    boolean leftL = (y == 0);
    
    if (!bottomL){ if (mat[x+1][y]){counter++;}} //Down
    if (!rightL){ if (mat[x][y+1]){counter++;}} //Right
    if (!bottomL && !rightL){ if (mat[x+1][y+1]){counter++;}} //DR
    
    if (!upperL){ if (mat[x-1][y]){counter++;}} //Up
    if (!leftL){ if (mat[x][y-1]){counter++;}} //Left
    if (!upperL && !leftL){ if (mat[x-1][y-1]){counter++;}} //UL
    
    if (!upperL && !rightL){ if (mat[x-1][y+1]){counter++;}} //UR
    if (!bottomL && !leftL){ if (mat[x+1][y-1]){counter++;}} //DL
    
    return counter;
  }
  
  //////////////////////////////////////////////////////////////////
  
  public static void newGen(){
    for (int x = 0; x < mat.length; x++){
      for(int y = 0; y < mat[0].length; y++){
        int n = check(x,y);
        //System.out.print(n + " "); //Debug
        if (mat[x][y] == true){
          if (n == 2 || n == 3) {matAlt[x][y] = true;}
          else {matAlt[x][y] = false;}
        }
        else{
          if (n == 3) {matAlt[x][y] = true;}
          else {matAlt[x][y] = false;}
        }
      } 
      //System.out.println(); //Debug
    }
    
    //Historial
    for (int a = 0; a < mat.length; a++){
      for (int b = 0; b < mat[0].length; b++){
        //System.out.println(a + " " + b + " " + gen%15); //Debug
        hist[gen%15][a][b] = mat[a][b];
      }
    }
    
    mat = matAlt;
    matAlt = new boolean[mat.length][mat[0].length];
  }
  
  //////////////////////////////////////////////////////////////////
  
  public static void readConfig(String s) throws IOException{
    BufferedReader fileIn = new BufferedReader(new FileReader("Templates/" + s + ".txt")); 
    
    for (int x = 0; x < 52; x++){
      String linea = fileIn.readLine() + " ";
      //System.out.println(linea); //Debug
      for (int y = 0; y < 52; y++){
        //System.out.println(linea.substring(y*2,y*2+2)); //Debug
        if (linea.substring(y*2,y*2+2).equals("X ")){mat[x][y] = true;}
        else {mat[x][y] = false;}
      }
    }
    
    fileIn.close();
  }
  
  //////////////////////////////////////////////////////////////////
  
  public static boolean menu() throws IOException{
    boolean error = false;
    
    System.out.println();
    System.out.println("(1) Configuracion aleatoria o (2) Cargar preset");
    int res = Integer.parseInt(stdIn.readLine());
    
    if (res == 1)
      fill();
    
    else {
      System.out.println();
      System.out.println("Presets disponibles: ");
      
      File folder = new File("Templates/");
      File[] listOfFiles = folder.listFiles();
      
      for (int x = 1; x < listOfFiles.length; x++) {
        System.out.println(listOfFiles[x].getName().substring(0,listOfFiles[x].getName().length()-4));
      }
      
      System.out.println();
      System.out.println("Nombre de archivo");
      String file = stdIn.readLine();
      
      try { readConfig(file); }
      catch (FileNotFoundException e){ 
        System.out.println("Archivo no encontrado");
        pegar = true;
        error = true;
      }
    }
    
    return error;
  }
  
  //////////////////////////////////////////////////////////////////
  
  public static boolean checkEst(){
    boolean found = false;
    int n = 0;
    
    while (!found && n < 15){
      found = compareMat(hist[n],mat);
      //System.out.println("Found: " + found + " Gen: " + gen + " N: " + n); //Debug
      //mostrar(); //Debug
      n++;
    }
    
    //System.out.println("n: " + n); //Debug
    return found;
  }
  
  //////////////////////////////////////////////////////////////////
  
  public static boolean compareMat(boolean a[][], boolean b[][]){
    boolean same = true;
    
    for (int x = 0; x < a.length; x++){
      for (int y = 0; y < a[0].length; y++){
        //System.out.println(a[x][y] + " " + b[x][y]); //Debug
        if (a[x][y] != b[x][y])
          same = false;
      }
    }
    
    return same;
  }
  
  //////////////////////////////////////////////////////////////////
  
  public static void checkDensity(){
    int cont = 0;
    
    for (int x = 0; x < mat.length; x++){
      for (int y = 0; y < mat[0].length; y++){
        if (mat[x][y] == true){cont++;}
      }
    }
    //System.out.println(cont);  //Debug
    density = cont;
  }
  
  //////////////////////////////////////////////////////////////////
  
  public static void mostrarHist(){
    for (int z = 0; z < 15; z++){
      System.out.print("Hist: " + z);
      for (int x = 0; x < mat.length; x++){
        for (int y = 0; y < mat[0].length; y++){
          if (hist[z][x][y]) System.out.print("X ");
          else System.out.print("  ");
        }
        System.out.println();
      }
      System.out.println("-----------------------------------");
    }
  }
  
  //////////////////////////////////////////////////////////////////
  
  public static void fiveCoord() throws IOException{
    ArrayList<Integer> possible = new ArrayList<Integer>();
    
    for (int x = 0; x < mat.length-4; x++){
      for (int y = 0; y < mat[0].length-3; y++){
        if (checkDetSpace(x,y)) {
          possible.add(x);
          possible.add(y);
        }
      }
    }
    
    /*
    System.out.println("All coord: ");  //Debug
    for (int z = 0; z < possible.size(); z+=2){
      System.out.println("(" + possible.get(z) + "," + possible.get(z+1) + ")");
    }
    */
    System.out.println("Numero de bombas: ");
    int bombs = Integer.parseInt(stdIn.readLine());
    
    for (int w = 0; w < bombs; w++){
      int index = (int)(Math.random()*possible.size()/2);
      
      //Spawn Bomb
      spawnBomb(possible.get(index), possible.get(index+1));

      //Remove coord from possible
      possible.remove(index+1);
      possible.remove(index);
    }
  }
  
  //////////////////////////////////////////////////////////////////
  
  public static boolean checkDetSpace(int a, int b){
    boolean space = true;
    
    for (int x = 0; x < 4; x++){
      for (int y = 0; y < 3; y++){
        if (mat[a+x][b+y]) {space = false;}
      }
    }
    
    return space;
  }
  
  //////////////////////////////////////////////////////////////////
  
  public static void spawnBomb(int x, int y){
    mat[x][y] = false;
    mat[x][y+1] = true;
    mat[x][y+2] = false;
    
    mat[x+1][y] = true;
    mat[x+1][y+1] = true;
    mat[x+1][y+2] = true;
    
    mat[x+2][y] = true;
    mat[x+2][y+1] = false;
    mat[x+2][y+2] = true;
    
    mat[x+3][y] = false;
    mat[x+3][y+1] = true;
    mat[x+3][y+2] = false;
    
  }
  
  //////////////////////////////////////////////////////////////////
  
  public static void main (String[ ] args) throws IOException, InterruptedException{
    
    boolean postDes = false;
    
    do{
      
      if (!postDes){
        gen = 0;
      }
      
      boolean b = true;
      boolean estable = false;
      boolean override = false;
    
      //Generacion de menu's
      while(b && !postDes){
        
        if (!pegar){
          for (int x = 0; x < 55; x++)
            System.out.println();
        }
        
        else
          pegar = false;
        
        b = menu();
      }
    
      //Main loop
      while (!estable || override){
      
        checkDensity();
        mostrar();
        newGen();
        gen++;
      
        //mostrarHist();
      
        estable = checkEst();
      
        //Prompt choice
      
        if (estable && !override){
          //System.out.println("Gen: " + gen);
          System.out.println();
          System.out.println("Sistema se ha esabilizado. Desea:");
          System.out.println("(1) Continuar, (2) Reiniciar, (3) Desastre");
        
          int c = Integer.parseInt(stdIn.readLine());
        
          if (c == 1)
            override = true;
          
          if (c == 3){
            fiveCoord();
            postDes = true;
          }
          
        }
   
        //Espacios entre generacion
        System.out.println("");
        for (int x = 0; x < 54 - mat.length /* - 1*/; x++){
          System.out.println();
        }
 
        //System.out.println("Gen: " + gen);
        Thread.sleep(100);
      }
      
    } while (true);
  }
}
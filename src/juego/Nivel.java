package juego;

import java.awt.Image;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.shape.Circle;

import javax.imageio.ImageIO;

public class Nivel {
	private File loc = null;
	private Image imagen= null;	
	private List<Rectangle> posArena = new ArrayList<Rectangle>();
	private List<Rectangle> posAgua = new ArrayList<Rectangle>();
	private List<Rectangle> posDash = new ArrayList<Rectangle>();
	private List<Rectangle> posTeleport = new ArrayList<Rectangle>();
	private List<Rectangle> posTroncos = new ArrayList<Rectangle>();
	private Circle agujero;
	private FileReader fr = null;  	 
    private BufferedReader br = null;

	Image getImagen() {
		return this.imagen;
	}
	
	//Evalua si esta en arena
	boolean estaEnArena(double x, double y) {
		for (int i=0 ; i<posArena.toArray().length ; i++) {
			if (posArena.get(i).contains(x,y)){
				return true;
			}
		}
		return false;
	}
	
	//Evalua si esta en agua
	boolean estaEnAgua(double x, double y) {
		for (int i=0 ; i<posAgua.toArray().length ; i++) {
			if (posAgua.get(i).contains(x,y)){
				return true;
			}
		}
		return false;
	}
	
	//Evalua si esta en dash
	boolean estaEnDash(double x, double y) {
		for (int i=0 ; i<posDash.toArray().length ; i++) {
			if (posDash.get(i).contains(x,y)){
				return true;
			}
		}
		return false;
	}
	
	//Evalua si esta en teleport
	boolean estaEnTeleport(double x, double y) {
		for (int i=0 ; i<posTeleport.toArray().length ; i++) {
			if (posTeleport.get(i).contains(x,y)){
				return true;
			}
		}
		return false;
	}
	
	//Evalua si esta en tronco
	boolean estaEnTronco(double x, double y) {
		for (int i=0 ; i<posTroncos.toArray().length ; i++) {
			if (posTroncos.get(i).contains(x,y)){
				return true;
			}
		}
		return false;
	}
	
	//Evalua si entro
	boolean entro(double x, double y) {
		if (agujero.contains(x, y)){
			return true;
		}
		return false;
	}
	
	//Genera las colisiones
	void generarRectangulo() {
		String line="";
		String[] nums;
		String guardarEn = "";
		Rectangle colision = null;
		try {				

			while ((line = br.readLine()) != null){
				//Elegi la lista a guardar
				if (line.contains("posArena")) guardarEn = "Arena";
				else if (line.contains("posAgua"))	guardarEn = "Agua";
				else if (line.contains("posDash"))	guardarEn = "Dash";
				else if (line.contains("posTeleport")) guardarEn = "Teleport";
				else if (line.contains("posTroncos")) guardarEn = "Troncos";
				else if (line.contains("agujero")) guardarEn = "Agujero";
				else{
					nums = line.split(",");
					if(nums.length==4)colision = new Rectangle(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]), Integer.parseInt(nums[2]),Integer.parseInt(nums[3]));
					//Guarda los datos
					if(guardarEn == "Arena" ) posArena.add(colision);
					else if(guardarEn == "Agua" ) posAgua.add(colision);
					else if(guardarEn == "Dash" ) posDash.add(colision);
					else if(guardarEn == "Teleport" ) posTeleport.add(colision);
					else if(guardarEn == "Troncos" ) posTroncos.add(colision);
					else if(guardarEn == "Agujero") agujero = new Circle(Integer.parseInt(nums[0]),Integer.parseInt(nums[1]),16);				
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
			}
	}
		
	Nivel(int nivelActual){
		try {
			fr = new FileReader("src/lvl/lvl" + nivelActual + ".txt");
			}
		catch (FileNotFoundException e){
		}
		br = new BufferedReader(fr);		
		loc = new File("src/lvl/lvl" + nivelActual + ".png");
		try { imagen = ImageIO.read(loc);}
		catch (IOException e){
			e.printStackTrace();
		}
		
		//Se generan las areas de colisiones
		generarRectangulo();		

	}
	
	
	
}
